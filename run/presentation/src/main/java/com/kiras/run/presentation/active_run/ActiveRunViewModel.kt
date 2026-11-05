package com.kiras.run.presentation.active_run

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.run.Run
import com.kiras.core.domain.settings.CountdownDuration
import com.kiras.core.domain.settings.SettingsRepository
import com.kiras.core.domain.util.Result
import com.kiras.core.presentation.ui.asUiText
import com.kiras.run.domain.LocationDataCalculator
import com.kiras.run.domain.RunningTracker
import com.kiras.core.domain.run.RunRepository
import com.kiras.run.presentation.active_run.service.ActiveRunService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.seconds

class ActiveRunViewModel(
    private val runningTracker: RunningTracker,
    private val runRepository: RunRepository,
    private val settingsRepository: SettingsRepository,
    private val applicationScope: CoroutineScope,
): ViewModel() {

    var state by mutableStateOf(ActiveRunState(
        shouldTrack = ActiveRunService.isServiceActive,
        hasStartedRunning = ActiveRunService.isServiceActive,
    ))
        private set

    private val eventChannel = Channel<ActiveRunEvent>()
    val events = eventChannel.receiveAsFlow()

    private var countdownJob: Job? = null

    private val shouldTrack = snapshotFlow { state.shouldTrack }
        .stateIn(viewModelScope, SharingStarted.Lazily, state.shouldTrack)
    private val hasLocationPermission = MutableStateFlow(false)

    private val isTracking = combine(
        shouldTrack,
        hasLocationPermission
    ) { shouldTrack, hasLocationPermission ->
        shouldTrack && hasLocationPermission
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        hasLocationPermission
            .onEach { hasPermission ->
                if(hasPermission) {
                    runningTracker.startObservingLocation()
                }
                else {
                    runningTracker.stopObservingLocation()
                }
            }
            .launchIn(viewModelScope)

        isTracking
            .onEach { isTracking ->
                runningTracker.setIsTracking(isTracking)
            }
            .launchIn(viewModelScope)

        runningTracker
            .currentLocation
            .onEach {
                state = state.copy(
                    currentLocation = it?.location
                )
            }
            .launchIn(viewModelScope)

        runningTracker
            .runData
            .onEach {
                state = state.copy(
                    runData = it,
                    maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(it.locations)
                )
            }
            .launchIn(viewModelScope)

        runningTracker
            .elapsedTime
            .onEach {
                state = state.copy(
                    elapsedTime = it
                )
            }
            .launchIn(viewModelScope)

        settingsRepository
            .getSettings()
            .onEach { settings ->
                state = state.copy(
                    distanceUnit = settings.distanceUnit,
                    paceFormat = settings.paceFormat,
                    countdown = settings.countdown,
                    keepScreenOn = settings.keepScreenOn
                )
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: ActiveRunAction) {
        when(action) {
            ActiveRunAction.OnBackRunClick -> {
                if(state.hasStartedRunning) {
                    state = state.copy(showFinishDialog = true)
                }
            }
            ActiveRunAction.OnFinishRunClick -> {
                state = state.copy(
                    isRunFinished = true,
                    isSavingRun = true
                )
            }
            ActiveRunAction.OnDismissFinishDialog -> {
                if(!state.isSavingRun) {
                    state = state.copy(showFinishDialog = false)
                }
            }
            ActiveRunAction.OnStopRunClick -> {
                state = state.copy(showFinishDialog = true)
            }
            ActiveRunAction.OnStartRunClick -> {
                when {
                    countdownJob != null -> cancelCountdown()
                    state.hasStartedRunning -> Unit
                    state.countdown != CountdownDuration.OFF -> {
                        startCountdown(state.countdown.seconds)
                    }
                    else -> {
                        state = state.copy(
                            hasStartedRunning = true,
                            shouldTrack = true
                        )
                    }
                }
            }
            ActiveRunAction.OnCancelCountdownClick -> cancelCountdown()
            is ActiveRunAction.OnSubmitLocationPermissionInfo -> {
                hasLocationPermission.value = action.acceptedLocationPermission
                state = state.copy(
                    showLocationPermissionRationale = action.showLocationPermissionRationale
                )
            }
            is ActiveRunAction.OnSubmitNotificationPermissionInfo -> {
                state = state.copy(
                    showNotificationPermissionRationale = action.showNotificationPermissionRationale
                )
            }
            ActiveRunAction.OnDismissRationaleDialog -> {
                state = state.copy(
                    showLocationPermissionRationale = false,
                    showNotificationPermissionRationale = false
                )
            }
            is ActiveRunAction.OnRunProcessed -> {
                finishRun(action.mapPictureBytes)
            }
            else -> Unit
        }
    }

    private fun finishRun(mapPictureBytes: ByteArray) {
        val locations = state.runData.locations
        viewModelScope.launch {
            if (locations.isEmpty() || locations.first().size <= 1) {
                state = state.copy(isSavingRun = false)
                eventChannel.send(ActiveRunEvent.TrySaveRunWithoutMove)

                applicationScope.async {
                    runningTracker.finishRun()
                }.await()
            }
            else {
                val run = Run(
                    id = null,
                    duration = state.elapsedTime,
                    dateTimeUtc = ZonedDateTime.now()
                        .withZoneSameInstant(ZoneId.of("UTC")),
                    distanceMeters = state.runData.distanceMeters,
                    location = state.currentLocation ?: Location(0.0, 0.0),
                    maxSpeedKmh = LocationDataCalculator.getMaxSpeedKmh(locations),
                    totalElevationMeters = LocationDataCalculator.getTotalElevationMeters(locations),
                    mapPictureUrl = null
                )

                runningTracker.finishRun()

                when (val result = runRepository.upsertRun(run, mapPictureBytes)) {
                    is Result.Error -> {
                        eventChannel.send(ActiveRunEvent.Error(result.error.asUiText()))
                    }

                    is Result.Success -> {
                        eventChannel.send(ActiveRunEvent.RunSaved)
                    }
                }

                state = state.copy(isSavingRun = false)
            }
        }
    }

    private fun startCountdown(seconds: Int) {
        countdownJob = viewModelScope.launch {
            for(remaining in seconds downTo 1) {
                state = state.copy(countdownRemaining = remaining)
                delay(1.seconds)
            }
            countdownJob = null
            state = state.copy(
                countdownRemaining = null,
                hasStartedRunning = true,
                shouldTrack = true
            )
        }
    }

    private fun cancelCountdown() {
        countdownJob?.cancel()
        countdownJob = null
        state = state.copy(countdownRemaining = null)
    }

    override fun onCleared() {
        super.onCleared()
        if(!ActiveRunService.isServiceActive) {
            runningTracker.stopObservingLocation()
        }
    }
}
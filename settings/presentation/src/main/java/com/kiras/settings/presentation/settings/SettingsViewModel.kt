package com.kiras.settings.presentation.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.core.domain.settings.SettingsRepository
import com.kiras.core.domain.settings.UserSettings
import com.kiras.core.domain.util.Result
import com.kiras.core.presentation.ui.asUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    var state by mutableStateOf(SettingsState())
        private set

    private val eventChannel = Channel<SettingsEvent>()
    val events = eventChannel.receiveAsFlow()

    private val saveMutex = Mutex()

    init {
        settingsRepository.getSettings()
            .onEach { settings ->
                state = state.copy(settings = settings)
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: SettingsAction) {
        when(action) {
            is SettingsAction.OnDistanceUnitChange -> {
                updateSettings { it.copy(distanceUnit = action.unit) }
            }
            is SettingsAction.OnPaceFormatChange -> {
                updateSettings { it.copy(paceFormat = action.format) }
            }
            is SettingsAction.OnKeepScreenOnToggle -> {
                updateSettings { it.copy(keepScreenOn = action.isEnabled) }
            }
            is SettingsAction.OnThemeChange -> {
                updateSettings { it.copy(theme = action.theme) }
            }
            SettingsAction.OnCountdownClick -> {
                state = state.copy(openPicker = SettingsPicker.COUNTDOWN)
            }
            SettingsAction.OnGpsAccuracyClick -> {
                state = state.copy(openPicker = SettingsPicker.GPS_ACCURACY)
            }
            is SettingsAction.OnCountdownSelect -> {
                state = state.copy(openPicker = null)
                updateSettings { it.copy(countdown = action.countdown) }
            }
            is SettingsAction.OnGpsAccuracySelect -> {
                state = state.copy(openPicker = null)
                updateSettings { it.copy(gpsAccuracy = action.accuracy) }
            }
            SettingsAction.OnPickerDismiss -> {
                state = state.copy(openPicker = null)
            }
            SettingsAction.OnHomeClick,
            SettingsAction.OnStatisticsClick -> Unit
        }
    }

    private fun updateSettings(update: (UserSettings) -> UserSettings) {
        state = state.copy(settings = update(state.settings))
        viewModelScope.launch {
            saveMutex.withLock {
                val result = settingsRepository.saveSettings(state.settings)
                if(result is Result.Error) {
                    eventChannel.send(SettingsEvent.Error(result.error.asUiText()))
                }
            }
        }
    }
}

package com.kiras.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.run.RunRepository
import com.kiras.core.domain.run.SyncRunScheduler
import com.kiras.run.presentation.run_overview.mapper.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import kotlin.time.Duration.Companion.minutes

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val syncRunScheduler: SyncRunScheduler,
    private val applicationScope: CoroutineScope,
    private val sessionStorage: SessionStorage
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        viewModelScope.launch {
            syncRunScheduler.scheduleSync(
                type = SyncRunScheduler.SyncType.FetchRuns(interval = 30.minutes)
            )
        }

        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUi() }
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)
        viewModelScope.launch {
            runRepository.syncPendingRuns()
            runRepository.fetchRuns()
        }
    }

    fun onAction(action: RunOverviewActions) {
        when(action) {
            is RunOverviewActions.DeleteRun -> {
                viewModelScope.launch(Dispatchers.IO) {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            RunOverviewActions.OnLogOutClick -> logout()
            RunOverviewActions.OnStartClick -> {}
            else -> Unit
        }
    }

    private fun logout() {
        applicationScope.launch {
            syncRunScheduler.cancelAllSyncs()
            runRepository.deleteAllRuns()
            runRepository.logout()
            sessionStorage.set(null)
        }
    }
}
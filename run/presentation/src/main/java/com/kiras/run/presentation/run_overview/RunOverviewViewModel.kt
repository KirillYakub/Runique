package com.kiras.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.run.RunRepository
import com.kiras.run.presentation.run_overview.mappers.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUi() }
            state = state.copy(runs = runsUi)
        }.launchIn(viewModelScope)
    }

    fun onAction(action: RunOverviewAction) {
        when(action) {
            is RunOverviewAction.OnDeleteRunClick -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            RunOverviewAction.OnLogOutClick -> logout()
            RunOverviewAction.OnStartClick -> {}
            else -> Unit
        }
    }

    private fun logout() {
        applicationScope.launch {
            sessionStorage.set(null)
        }
    }
}

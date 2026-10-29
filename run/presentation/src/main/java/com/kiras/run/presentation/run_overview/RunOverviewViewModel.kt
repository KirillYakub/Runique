package com.kiras.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.run.RunRepository
import com.kiras.core.domain.user.UserRepository
import com.kiras.core.domain.util.Result
import com.kiras.run.presentation.run_overview.mappers.toRunUi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val userRepository: UserRepository,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    init {
        runRepository.getRuns().onEach { runs ->
            val runsUi = runs.map { it.toRunUi() }
            state = state.copy(
                runs = runsUi,
                lastRun = runsUi.firstOrNull()
            )
        }.launchIn(viewModelScope)

        loadUsername()
    }

    fun onAction(action: RunOverviewAction) {
        when(action) {
            is RunOverviewAction.OnDeleteRunClick -> {
                viewModelScope.launch {
                    runRepository.deleteRun(action.runUi.id)
                }
            }
            RunOverviewAction.OnAccountClick -> {
                state = state.copy(isAccountMenuOpen = true)
            }
            RunOverviewAction.OnAccountMenuDismiss -> {
                state = state.copy(isAccountMenuOpen = false)
            }
            RunOverviewAction.OnLogOutClick -> {
                state = state.copy(isAccountMenuOpen = false)
                logout()
            }
            RunOverviewAction.OnSeeAllRunsClick -> {
                state = state.copy(isAllRunsOpen = true)
            }
            RunOverviewAction.OnAllRunsBackClick -> {
                state = state.copy(isAllRunsOpen = false)
            }
            RunOverviewAction.OnStartClick,
            RunOverviewAction.OnStatisticsClick -> Unit
        }
    }

    private fun loadUsername() {
        viewModelScope.launch {
            val userId = sessionStorage.get()?.userId ?: return@launch
            val result = userRepository.getUser(userId)
            if(result is Result.Success) {
                state = state.copy(username = result.data.username)
            }
        }
    }

    private fun logout() {
        applicationScope.launch {
            sessionStorage.set(null)
        }
    }
}

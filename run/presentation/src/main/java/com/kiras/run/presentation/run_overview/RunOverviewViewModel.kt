package com.kiras.run.presentation.run_overview

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.run.RunRepository
import com.kiras.core.domain.settings.SettingsRepository
import com.kiras.core.domain.user.UserRepository
import com.kiras.core.domain.util.Result
import com.kiras.core.presentation.ui.asUiText
import com.kiras.run.domain.goal.DailyGoal
import com.kiras.run.domain.goal.DailyGoalProgressCalculator
import com.kiras.run.domain.goal.DailyGoalRepository
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.run_overview.mappers.toDailyGoal
import com.kiras.run.presentation.run_overview.mappers.toDailyGoalUi
import com.kiras.run.presentation.run_overview.mappers.toGoalDraftUi
import com.kiras.run.presentation.run_overview.mappers.toRunUi
import com.kiras.run.presentation.run_overview.model.GoalDraftUi
import com.kiras.run.presentation.util.toStepperConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class RunOverviewViewModel(
    private val runRepository: RunRepository,
    private val userRepository: UserRepository,
    private val dailyGoalRepository: DailyGoalRepository,
    private val settingsRepository: SettingsRepository,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : ViewModel() {

    var state by mutableStateOf(RunOverviewState())
        private set

    private val eventChannel = Channel<RunOverviewEvent>()
    val events = eventChannel.receiveAsFlow()

    private var savedGoal: DailyGoal? = null

    init {
        combine(
            runRepository.getRuns(),
            dailyGoalRepository.getDailyGoal(),
            settingsRepository.getSettings()
        ) { runs, goal, settings ->
            savedGoal = goal
            val runsUi = runs.map { it.toRunUi(settings) }
            val today = LocalDate.now()
            state = state.copy(
                runs = runsUi,
                lastRun = runsUi.firstOrNull(),
                dailyGoal = goal?.let {
                    DailyGoalProgressCalculator
                        .calculate(it, runs, today, ZoneId.systemDefault())
                        .toDailyGoalUi(it.metric, settings.distanceUnit, today)
                },
                isDailyGoalLoaded = true,
                distanceUnit = settings.distanceUnit
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
            RunOverviewAction.OnEditGoalClick -> {
                if(!state.isDailyGoalLoaded) return
                state = state.copy(
                    goalDraft = savedGoal?.toGoalDraftUi() ?: GoalDraftUi(
                        target = GoalMetric.DISTANCE.toStepperConfig(state.distanceUnit).defaultTarget
                    ),
                    isGoalSheetOpen = true
                )
            }
            RunOverviewAction.OnGoalSheetDismiss -> {
                state = state.copy(isGoalSheetOpen = false)
            }
            is RunOverviewAction.OnGoalMetricChange -> {
                if(action.metric != state.goalDraft.metric) {
                    updateGoalDraft {
                        it.copy(
                            metric = action.metric,
                            target = action.metric.toStepperConfig(state.distanceUnit).defaultTarget
                        )
                    }
                }
            }
            RunOverviewAction.OnGoalIncrementClick -> {
                updateGoalDraft { draft ->
                    val config = draft.metric.toStepperConfig(state.distanceUnit)
                    draft.copy(target = config.increment(draft.target))
                }
            }
            RunOverviewAction.OnGoalDecrementClick -> {
                updateGoalDraft { draft ->
                    val config = draft.metric.toStepperConfig(state.distanceUnit)
                    draft.copy(target = config.decrement(draft.target))
                }
            }
            is RunOverviewAction.OnGoalPresetClick -> {
                updateGoalDraft { it.copy(target = action.target) }
            }
            is RunOverviewAction.OnGoalDayToggle -> {
                updateGoalDraft { draft ->
                    val goalDays = if(action.day in draft.goalDays) {
                        draft.goalDays - action.day
                    } else {
                        draft.goalDays + action.day
                    }
                    draft.copy(goalDays = goalDays)
                }
            }
            RunOverviewAction.OnSaveGoalClick -> saveGoal()
            RunOverviewAction.OnStartClick,
            RunOverviewAction.OnStatisticsClick -> Unit
        }
    }

    private fun updateGoalDraft(update: (GoalDraftUi) -> GoalDraftUi) {
        state = state.copy(goalDraft = update(state.goalDraft))
    }

    private fun saveGoal() {
        val draft = state.goalDraft
        if(draft.goalDays.isEmpty()) return
        state = state.copy(isGoalSheetOpen = false)
        viewModelScope.launch {
            val result = dailyGoalRepository.saveDailyGoal(draft.toDailyGoal())
            if(result is Result.Error) {
                eventChannel.send(RunOverviewEvent.Error(result.error.asUiText()))
            }
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

package com.kiras.run.presentation.run_overview

import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.run_overview.model.RunUi
import java.time.DayOfWeek

sealed interface RunOverviewAction {
    data object OnStartClick: RunOverviewAction
    data object OnLogOutClick: RunOverviewAction
    data object OnStatisticsClick: RunOverviewAction
    data object OnSettingsClick: RunOverviewAction
    data object OnAccountClick: RunOverviewAction
    data object OnAccountMenuDismiss: RunOverviewAction
    data object OnSeeAllRunsClick: RunOverviewAction
    data object OnAllRunsBackClick: RunOverviewAction
    data class OnDeleteRunClick(val runUi: RunUi): RunOverviewAction
    data object OnEditGoalClick: RunOverviewAction
    data object OnGoalSheetDismiss: RunOverviewAction
    data class OnGoalMetricChange(val metric: GoalMetric): RunOverviewAction
    data object OnGoalIncrementClick: RunOverviewAction
    data object OnGoalDecrementClick: RunOverviewAction
    data class OnGoalPresetClick(val target: Long): RunOverviewAction
    data class OnGoalDayToggle(val day: DayOfWeek): RunOverviewAction
    data object OnSaveGoalClick: RunOverviewAction
}

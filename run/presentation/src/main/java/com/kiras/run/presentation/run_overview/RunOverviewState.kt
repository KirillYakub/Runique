package com.kiras.run.presentation.run_overview

import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.presentation.run_overview.model.DailyGoalUi
import com.kiras.run.presentation.run_overview.model.GoalDraftUi
import com.kiras.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList(),
    val lastRun: RunUi? = null,
    val username: String = "",
    val isAccountMenuOpen: Boolean = false,
    val isAllRunsOpen: Boolean = false,
    val dailyGoal: DailyGoalUi? = null,
    val isDailyGoalLoaded: Boolean = false,
    val isGoalSheetOpen: Boolean = false,
    val goalDraft: GoalDraftUi = GoalDraftUi(),
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val expandedRunIds: Set<String> = emptySet(),
    val monthRunCount: Int = 0,
    val monthDistance: String = ""
)

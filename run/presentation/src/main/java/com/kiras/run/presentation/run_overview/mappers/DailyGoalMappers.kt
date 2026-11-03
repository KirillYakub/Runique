package com.kiras.run.presentation.run_overview.mappers

import com.kiras.run.domain.goal.DailyGoal
import com.kiras.run.presentation.run_overview.model.GoalDraftUi

fun DailyGoal.toGoalDraftUi(): GoalDraftUi {
    return GoalDraftUi(
        metric = metric,
        target = target,
        goalDays = goalDays
    )
}

fun GoalDraftUi.toDailyGoal(): DailyGoal {
    return DailyGoal(
        metric = metric,
        target = target,
        goalDays = goalDays
    )
}

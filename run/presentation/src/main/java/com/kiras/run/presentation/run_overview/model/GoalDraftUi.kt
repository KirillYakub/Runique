package com.kiras.run.presentation.run_overview.model

import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.util.toStepperConfig
import java.time.DayOfWeek

data class GoalDraftUi(
    val metric: GoalMetric = GoalMetric.DISTANCE,
    val target: Long = GoalMetric.DISTANCE.toStepperConfig(DistanceUnit.KILOMETERS).defaultTarget,
    val goalDays: Set<DayOfWeek> = setOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )
)

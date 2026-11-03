package com.kiras.run.domain.goal

import java.time.DayOfWeek

data class DailyGoal(
    val metric: GoalMetric,
    val target: Long,
    val goalDays: Set<DayOfWeek>
)

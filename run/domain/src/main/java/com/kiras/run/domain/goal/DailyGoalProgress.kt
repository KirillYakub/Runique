package com.kiras.run.domain.goal

data class DailyGoalProgress(
    val todayValue: Long,
    val target: Long,
    val week: List<DayGoalStatus>,
    val streakDays: Int
)

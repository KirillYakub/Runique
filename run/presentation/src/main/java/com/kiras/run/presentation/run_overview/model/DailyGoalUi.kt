package com.kiras.run.presentation.run_overview.model

import com.kiras.core.presentation.ui.UiText
import com.kiras.run.domain.goal.DayGoalStatus

data class DailyGoalUi(
    val todayValue: String,
    val target: String,
    val unit: UiText,
    val remaining: UiText,
    val progress: Float,
    val percent: String,
    val week: List<DayGoalStatus>,
    val todayIndex: Int,
    val streakDays: Int
)

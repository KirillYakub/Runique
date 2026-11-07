package com.kiras.statistics.presentation.dashboard.model

import androidx.compose.runtime.Stable

@Stable
data class WeekActivityUi(
    val total: StatValueUi = StatValueUi(),
    val changePercent: Int? = null,
    val barFractions: List<Float> = List(7) { 0f },
    val dayDistances: List<String> = List(7) { "" },
    val todayIndex: Int = 0
)

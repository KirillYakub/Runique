package com.kiras.statistics.presentation.dashboard.model

import androidx.compose.runtime.Stable

@Stable
data class PaceTrendUi(
    val headline: StatValueUi = StatValueUi(),
    val pointFractions: List<Float?> = List(8) { null },
    val rangeText: String? = null,
    val changeAmount: String? = null,
    val direction: TrendDirection? = null
) {
    val hasData: Boolean
        get() = pointFractions.any { it != null }
}

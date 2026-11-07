package com.kiras.statistics.presentation.dashboard

import androidx.compose.runtime.Stable
import com.kiras.statistics.presentation.dashboard.model.PaceTrendUi
import com.kiras.statistics.presentation.dashboard.model.StatValueUi
import com.kiras.statistics.presentation.dashboard.model.WeekActivityUi

@Stable
data class StatisticsDashboardState(
    val isLoading: Boolean = true,
    val runCount: Int = 0,
    val totalDistance: StatValueUi = StatValueUi(),
    val totalTime: StatValueUi = StatValueUi(),
    val fastestRun: StatValueUi = StatValueUi(),
    val avgDistance: StatValueUi = StatValueUi(),
    val avgPaceOrSpeed: StatValueUi = StatValueUi(),
    val isSpeedShown: Boolean = false,
    val weekActivity: WeekActivityUi = WeekActivityUi(),
    val paceTrend: PaceTrendUi = PaceTrendUi()
)

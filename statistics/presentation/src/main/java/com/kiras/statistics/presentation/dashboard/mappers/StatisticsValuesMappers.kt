package com.kiras.statistics.presentation.dashboard.mappers

import com.kiras.core.presentation.ui.formatted
import com.kiras.core.presentation.ui.toFormattedKm
import com.kiras.core.presentation.ui.toFormattedKmh
import com.kiras.statistics.domain.StatisticsValues
import com.kiras.statistics.presentation.dashboard.StatisticsDashboardState
import com.kiras.statistics.presentation.util.toFormattedTotalTime
import kotlin.time.Duration.Companion.seconds

fun StatisticsValues.toStatisticsDashboardState(): StatisticsDashboardState {
    return StatisticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fastestEverRun.toFormattedKmh(),
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKm(),
        avgPace = avgPacePerRun.seconds.formatted()
    )
}

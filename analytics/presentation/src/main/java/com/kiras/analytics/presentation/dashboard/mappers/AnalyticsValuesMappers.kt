package com.kiras.analytics.presentation.dashboard.mappers

import com.kiras.analytics.domain.AnalyticsValues
import com.kiras.analytics.presentation.dashboard.AnalyticsDashboardState
import com.kiras.analytics.presentation.util.toFormattedTotalTime
import com.kiras.core.presentation.ui.formatted
import com.kiras.core.presentation.ui.toFormattedKm
import com.kiras.core.presentation.ui.toFormattedKmh
import kotlin.time.Duration.Companion.seconds

fun AnalyticsValues.toAnalyticsDashboardState(): AnalyticsDashboardState {
    return AnalyticsDashboardState(
        totalDistanceRun = (totalDistanceRun / 1000.0).toFormattedKm(),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fastestEverRun.toFormattedKmh(),
        avgDistance = (avgDistancePerRun / 1000.0).toFormattedKm(),
        avgPace = avgPacePerRun.seconds.formatted()
    )
}

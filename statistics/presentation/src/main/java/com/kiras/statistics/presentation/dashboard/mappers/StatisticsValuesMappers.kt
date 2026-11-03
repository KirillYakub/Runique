package com.kiras.statistics.presentation.dashboard.mappers

import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.domain.settings.UserSettings
import com.kiras.core.presentation.ui.toFormattedDistance
import com.kiras.core.presentation.ui.toFormattedPacePerUnit
import com.kiras.core.presentation.ui.toFormattedSpeed
import com.kiras.statistics.domain.StatisticsValues
import com.kiras.statistics.presentation.dashboard.StatisticsDashboardState
import com.kiras.statistics.presentation.util.toFormattedTotalTime
import kotlin.time.Duration.Companion.minutes

fun StatisticsValues.toStatisticsDashboardState(settings: UserSettings): StatisticsDashboardState {
    val unit = settings.distanceUnit
    return StatisticsDashboardState(
        totalDistanceRun = totalDistanceRun.toFormattedDistance(unit),
        totalTimeRun = totalTimeRun.toFormattedTotalTime(),
        fastestEverRun = fastestEverRun.toFormattedSpeed(unit),
        avgDistance = avgDistancePerRun.toFormattedDistance(unit),
        avgPace = when(settings.paceFormat) {
            PaceFormat.PACE -> (avgPacePerRun * unit.metersPerUnit / 1000).minutes
                .toFormattedPacePerUnit(unit)
            PaceFormat.SPEED -> {
                val avgSpeedKmh = if(avgPacePerRun > 0.0) 60 / avgPacePerRun else 0.0
                avgSpeedKmh.toFormattedSpeed(unit)
            }
        },
        isAvgSpeedShown = settings.paceFormat == PaceFormat.SPEED
    )
}

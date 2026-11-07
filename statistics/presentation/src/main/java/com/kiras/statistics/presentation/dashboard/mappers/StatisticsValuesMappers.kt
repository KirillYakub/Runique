package com.kiras.statistics.presentation.dashboard.mappers

import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.domain.settings.UserSettings
import com.kiras.statistics.domain.StatisticsValues
import com.kiras.statistics.domain.WeekSummary
import com.kiras.statistics.presentation.dashboard.StatisticsDashboardState
import com.kiras.statistics.presentation.dashboard.model.PaceTrendUi
import com.kiras.statistics.presentation.dashboard.model.TrendDirection
import com.kiras.statistics.presentation.dashboard.model.WeekActivityUi
import com.kiras.statistics.presentation.util.emptyPerformanceStat
import com.kiras.statistics.presentation.util.toDisplayedPerformance
import com.kiras.statistics.presentation.util.toDistanceStat
import com.kiras.statistics.presentation.util.toPerformanceChange
import com.kiras.statistics.presentation.util.toPerformanceStat
import com.kiras.statistics.presentation.util.toSpeedStat
import com.kiras.statistics.presentation.util.toTotalTimeStat
import kotlin.math.ceil

fun StatisticsValues.toStatisticsDashboardState(settings: UserSettings): StatisticsDashboardState {
    val unit = settings.distanceUnit
    val isSpeedShown = settings.paceFormat == PaceFormat.SPEED
    return StatisticsDashboardState(
        isLoading = false,
        runCount = runCount,
        totalDistance = totalDistanceRun.toDouble().toDistanceStat(unit),
        totalTime = totalTimeRun.toTotalTimeStat(),
        fastestRun = fastestEverRun.toSpeedStat(unit),
        avgDistance = avgDistancePerRun.toDistanceStat(unit),
        avgPaceOrSpeed = avgPaceSecondsPerKm
            ?.toDisplayedPerformance(unit, isSpeedShown)
            ?.toPerformanceStat(unit, isSpeedShown)
            ?: emptyPerformanceStat(unit, isSpeedShown),
        isSpeedShown = isSpeedShown,
        weekActivity = thisWeek.toWeekActivityUi(unit),
        paceTrend = weeklyPaceSecondsPerKm.toPaceTrendUi(unit, isSpeedShown)
    )
}

private fun WeekSummary.toWeekActivityUi(unit: DistanceUnit): WeekActivityUi {
    val peak = (dailyDistanceMeters.maxOrNull() ?: 0) / unit.metersPerUnit
    val axisMax = (ceil(peak / 3) * 3).coerceAtLeast(3.0)
    return WeekActivityUi(
        total = totalMeters.toDouble().toDistanceStat(unit),
        changePercent = changePercent,
        barFractions = dailyDistanceMeters.map { meters ->
            (meters / unit.metersPerUnit / axisMax).toFloat()
        },
        dayDistances = dailyDistanceMeters.map { meters ->
            val distance = meters.toDouble().toDistanceStat(unit)
            "${distance.value} ${distance.unit}"
        },
        todayIndex = todayIndex
    )
}

private fun List<Double?>.toPaceTrendUi(unit: DistanceUnit, isSpeedShown: Boolean): PaceTrendUi {
    val displayed = map { it?.toDisplayedPerformance(unit, isSpeedShown) }
    val present = displayed.filterNotNull()
    val first = present.firstOrNull()
    val latest = present.lastOrNull()
    if(first == null || latest == null) {
        return PaceTrendUi(headline = emptyPerformanceStat(unit, isSpeedShown))
    }

    val slowest = if(isSpeedShown) present.min() else present.max()
    val fastest = if(isSpeedShown) present.max() else present.min()
    val pointFractions = displayed.map { value ->
        value?.let {
            if(fastest == slowest) 0.5f else ((it - slowest) / (fastest - slowest)).toFloat()
        }
    }
    val headline = latest.toPerformanceStat(unit, isSpeedShown)
    if(present.size < 2) {
        return PaceTrendUi(headline = headline, pointFractions = pointFractions)
    }

    val firstStat = first.toPerformanceStat(unit, isSpeedShown)
    return PaceTrendUi(
        headline = headline,
        pointFractions = pointFractions,
        rangeText = "${firstStat.value} → ${headline.value} ${headline.unit}",
        changeAmount = (latest - first).toPerformanceChange(unit, isSpeedShown),
        direction = when {
            latest == first -> TrendDirection.UNCHANGED
            (latest > first) == isSpeedShown -> TrendDirection.IMPROVED
            else -> TrendDirection.WORSENED
        }
    )
}

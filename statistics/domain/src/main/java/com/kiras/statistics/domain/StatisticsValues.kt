package com.kiras.statistics.domain

import kotlin.time.Duration

data class StatisticsValues(
    val runCount: Int = 0,
    val totalDistanceRun: Int = 0,
    val totalTimeRun: Duration = Duration.ZERO,
    val fastestEverRun: Double = 0.0,
    val avgDistancePerRun: Double = 0.0,
    val avgPaceSecondsPerKm: Double? = null,
    val thisWeek: WeekSummary = WeekSummary(),
    val weeklyPaceSecondsPerKm: List<Double?> = List(8) { null }
)

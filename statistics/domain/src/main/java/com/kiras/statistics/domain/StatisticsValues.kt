package com.kiras.statistics.domain

import kotlin.time.Duration

data class StatisticsValues (
    val totalDistanceRun: Int = 0,
    val totalTimeRun: Duration = Duration.ZERO,
    val fastestEverRun: Double = 0.0,
    val avgDistancePerRun: Double = 0.0,
    val avgPacePerRun: Double = 0.0
)

package com.kiras.statistics.domain

import java.time.Instant
import kotlin.time.Duration

data class RunStatsEntry(
    val startedAt: Instant,
    val distanceMeters: Int,
    val duration: Duration,
    val maxSpeedKmh: Double
)

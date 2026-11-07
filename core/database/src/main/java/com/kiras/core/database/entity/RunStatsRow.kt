package com.kiras.core.database.entity

data class RunStatsRow(
    val dateTimeUtc: String,
    val distanceMeters: Int,
    val durationMillis: Long,
    val maxSpeedKm: Double
)

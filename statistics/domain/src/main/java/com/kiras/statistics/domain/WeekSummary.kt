package com.kiras.statistics.domain

data class WeekSummary(
    val dailyDistanceMeters: List<Int> = List(7) { 0 },
    val totalMeters: Int = 0,
    val todayIndex: Int = 0,
    val changePercent: Int? = null
)

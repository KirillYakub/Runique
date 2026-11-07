package com.kiras.statistics.presentation.util

import com.kiras.statistics.presentation.dashboard.model.StatValueUi
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.toTotalTimeStat(): StatValueUi {
    val hours = toLong(DurationUnit.HOURS)
    val minutes = toLong(DurationUnit.MINUTES) % 60

    return StatValueUi(
        value = if(hours > 0) "$hours h $minutes" else "$minutes",
        unit = "min"
    )
}

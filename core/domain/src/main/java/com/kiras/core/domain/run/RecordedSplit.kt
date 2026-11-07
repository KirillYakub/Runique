package com.kiras.core.domain.run

import com.kiras.core.domain.settings.DistanceUnit
import kotlin.time.Duration

data class RecordedSplit(
    val unit: DistanceUnit,
    val number: Int,
    val distanceMeters: Double,
    val duration: Duration
)

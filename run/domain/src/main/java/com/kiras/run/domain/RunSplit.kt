package com.kiras.run.domain

import com.kiras.core.domain.location.Location
import kotlin.time.Duration

data class RunSplit(
    val number: Int,
    val duration: Duration,
    val endLocation: Location
)

package com.kiras.run.presentation.active_run.model

import com.kiras.core.domain.location.Location

data class RunSplitUi(
    val number: Int,
    val valueText: String,
    val barFraction: Float,
    val isFastest: Boolean,
    val endLocation: Location
)

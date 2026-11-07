package com.kiras.run.presentation.run_overview.model

data class RunSplitRowUi(
    val label: String,
    val valueText: String,
    val barFraction: Float,
    val isFastest: Boolean,
    val isPartial: Boolean
)

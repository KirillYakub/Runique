package com.kiras.run.presentation.run_overview.model

import androidx.annotation.StringRes
import com.kiras.core.domain.settings.PaceFormat

data class RunSplitsUi(
    val rows: List<RunSplitRowUi>,
    @StringRes val labelRes: Int,
    @StringRes val unitLabelRes: Int,
    val paceFormat: PaceFormat,
    val averageText: String,
    val fastestNumber: Int?,
    val fastestValueText: String?
)

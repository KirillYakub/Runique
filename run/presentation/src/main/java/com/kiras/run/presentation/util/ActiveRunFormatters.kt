package com.kiras.run.presentation.util

import androidx.annotation.StringRes
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.presentation.R
import java.util.Locale

fun Int.toDistanceValueText(unit: DistanceUnit): String {
    return String.format(Locale.getDefault(), "%.2f", this / unit.metersPerUnit)
}

@StringRes
fun DistanceUnit.toUnitLabelRes(): Int {
    return when(this) {
        DistanceUnit.KILOMETERS -> R.string.unit_km
        DistanceUnit.MILES -> R.string.unit_mi
    }
}

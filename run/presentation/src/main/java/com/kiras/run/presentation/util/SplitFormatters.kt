package com.kiras.run.presentation.util

import androidx.annotation.StringRes
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.presentation.ui.toFormattedAvgSpeed
import com.kiras.run.presentation.R
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

fun Duration.toSplitValueText(
    distanceMeters: Double,
    unit: DistanceUnit,
    paceFormat: PaceFormat
): String {
    return when(paceFormat) {
        PaceFormat.PACE -> {
            val totalSeconds = toDouble(DurationUnit.SECONDS).roundToInt()
            String.format(Locale.getDefault(), "%d:%02d", totalSeconds / 60, totalSeconds % 60)
        }
        PaceFormat.SPEED -> toFormattedAvgSpeed(distanceMeters.roundToInt(), unit)
    }
}

@StringRes
fun DistanceUnit.toSplitsLabelRes(paceFormat: PaceFormat): Int {
    return when(paceFormat) {
        PaceFormat.PACE -> when(this) {
            DistanceUnit.KILOMETERS -> R.string.splits_label_min_per_km
            DistanceUnit.MILES -> R.string.splits_label_min_per_mi
        }
        PaceFormat.SPEED -> when(this) {
            DistanceUnit.KILOMETERS -> R.string.splits_label_km_per_hour
            DistanceUnit.MILES -> R.string.splits_label_miles_per_hour
        }
    }
}

fun Duration.toBarFraction(fastest: Duration): Float {
    if(!fastest.isPositive()) {
        return 1f
    }
    return (1.0 - 2.5 * ((this - fastest) / fastest))
        .coerceIn(0.15, 1.0)
        .toFloat()
}

fun Double.toPartialSplitLabel(unit: DistanceUnit): String {
    return String.format(Locale.getDefault(), "%.2f", this / unit.metersPerUnit)
}

package com.kiras.settings.presentation.util

import androidx.annotation.StringRes
import com.kiras.core.domain.settings.AppTheme
import com.kiras.core.domain.settings.CountdownDuration
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.GpsAccuracy
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.presentation.ui.UiText
import com.kiras.settings.presentation.R

@StringRes
fun DistanceUnit.toLabelRes(): Int {
    return when(this) {
        DistanceUnit.KILOMETERS -> R.string.unit_km
        DistanceUnit.MILES -> R.string.unit_mi
    }
}

@StringRes
fun PaceFormat.toLabelRes(distanceUnit: DistanceUnit): Int {
    return when(this) {
        PaceFormat.PACE -> when(distanceUnit) {
            DistanceUnit.KILOMETERS -> R.string.pace_min_per_km
            DistanceUnit.MILES -> R.string.pace_min_per_mi
        }
        PaceFormat.SPEED -> when(distanceUnit) {
            DistanceUnit.KILOMETERS -> R.string.speed_km_per_hour
            DistanceUnit.MILES -> R.string.speed_miles_per_hour
        }
    }
}

fun CountdownDuration.toLabel(): UiText {
    return when(this) {
        CountdownDuration.OFF -> UiText.StringResource(R.string.countdown_off)
        else -> UiText.StringResource(R.string.countdown_seconds, arrayOf(seconds))
    }
}

@StringRes
fun GpsAccuracy.toLabelRes(): Int {
    return when(this) {
        GpsAccuracy.HIGH -> R.string.gps_high
        GpsAccuracy.BALANCED -> R.string.gps_balanced
        GpsAccuracy.LOW -> R.string.gps_low
    }
}

@StringRes
fun AppTheme.toLabelRes(): Int {
    return when(this) {
        AppTheme.SYSTEM -> R.string.theme_system
        AppTheme.NIGHT -> R.string.theme_night
        AppTheme.DAY -> R.string.theme_day
    }
}

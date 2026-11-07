package com.kiras.core.presentation.ui

import android.annotation.SuppressLint
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@SuppressLint("DefaultLocale")
fun Duration.formatted(): String {
    val totalSeconds = inWholeSeconds
    val hours = String.format("%02d", totalSeconds / (60 * 60))
    val minutes = String.format("%02d", (totalSeconds % 3600) / 60)
    val seconds = String.format("%02d", totalSeconds % 60)

    return "$hours:$minutes:$seconds"
}

fun Double.toFormattedDistance(unit: DistanceUnit): String {
    return "${(this / unit.metersPerUnit).roundToDecimals(1)} ${unit.toDistanceLabel()}"
}

fun Int.toFormattedDistance(unit: DistanceUnit): String {
    return toDouble().toFormattedDistance(unit)
}

fun Int.toFormattedPreciseDistance(unit: DistanceUnit): String {
    return "${(this / unit.metersPerUnit).roundToDecimals(2)} ${unit.toDistanceLabel()}"
}

fun Duration.toFormattedPace(distanceMeters: Int, unit: DistanceUnit): String {
    val distanceInUnits = distanceMeters / unit.metersPerUnit
    if(this == Duration.ZERO || distanceInUnits <= 0.0) {
        return "-"
    }
    return (inWholeSeconds / distanceInUnits).seconds.toFormattedPacePerUnit(unit)
}

@SuppressLint("DefaultLocale")
fun Duration.toFormattedPacePerUnit(unit: DistanceUnit): String {
    val secondsPerUnit = toDouble(DurationUnit.SECONDS).roundToInt()
    if(secondsPerUnit <= 0) {
        return "-"
    }
    val paceMinutes = secondsPerUnit / 60
    val paceSeconds = String.format("%02d", secondsPerUnit % 60)
    return "$paceMinutes:$paceSeconds / ${unit.toDistanceLabel()}"
}

fun Double.toFormattedSpeed(unit: DistanceUnit): String {
    return "${(this * 1000 / unit.metersPerUnit).roundToDecimals(1)} ${unit.toSpeedLabel()}"
}

fun Duration.toFormattedAvgSpeed(distanceMeters: Int, unit: DistanceUnit): String {
    val hours = toDouble(DurationUnit.HOURS)
    if(hours <= 0.0) {
        return "-"
    }
    return (distanceMeters / 1000.0 / hours).toFormattedSpeed(unit)
}

fun Duration.toFormattedPaceOrSpeed(
    distanceMeters: Int,
    unit: DistanceUnit,
    paceFormat: PaceFormat
): String {
    return when(paceFormat) {
        PaceFormat.PACE -> toFormattedPace(distanceMeters, unit)
        PaceFormat.SPEED -> toFormattedAvgSpeed(distanceMeters, unit)
    }
}

fun Int.toFormattedMeters(): String {
    return "$this m"
}

private fun DistanceUnit.toDistanceLabel(): String {
    return when(this) {
        DistanceUnit.KILOMETERS -> "km"
        DistanceUnit.MILES -> "mi"
    }
}

private fun DistanceUnit.toSpeedLabel(): String {
    return when(this) {
        DistanceUnit.KILOMETERS -> "km/h"
        DistanceUnit.MILES -> "mph"
    }
}

private fun Double.roundToDecimals(decimalCount: Int): Double {
    val factor = 10f.pow(decimalCount)
    return round(this * factor) / factor
}

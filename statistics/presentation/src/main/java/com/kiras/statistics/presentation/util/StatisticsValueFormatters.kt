package com.kiras.statistics.presentation.util

import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.presentation.ui.toDistanceLabel
import com.kiras.core.presentation.ui.toSpeedLabel
import com.kiras.statistics.presentation.dashboard.model.StatValueUi
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.roundToInt

fun Double.toDistanceStat(unit: DistanceUnit): StatValueUi {
    return StatValueUi(
        value = (this / unit.metersPerUnit).roundToOneDecimal().toString(),
        unit = unit.toDistanceLabel()
    )
}

fun Double.toSpeedStat(unit: DistanceUnit): StatValueUi {
    return StatValueUi(
        value = (this * 1000 / unit.metersPerUnit).roundToOneDecimal().toString(),
        unit = unit.toSpeedLabel()
    )
}

fun Double.toDisplayedPerformance(unit: DistanceUnit, isSpeedShown: Boolean): Double {
    return if(isSpeedShown) {
        (3600 / this * 1000 / unit.metersPerUnit).roundToOneDecimal()
    } else {
        round(this * unit.metersPerUnit / 1000)
    }
}

fun Double.toPerformanceStat(unit: DistanceUnit, isSpeedShown: Boolean): StatValueUi {
    return if(isSpeedShown) {
        StatValueUi(value = toString(), unit = unit.toSpeedLabel())
    } else {
        val seconds = roundToInt()
        StatValueUi(
            value = "${seconds / 60}:${(seconds % 60).toString().padStart(2, '0')}",
            unit = "/${unit.toDistanceLabel()}"
        )
    }
}

fun emptyPerformanceStat(unit: DistanceUnit, isSpeedShown: Boolean): StatValueUi {
    return StatValueUi(
        value = "-",
        unit = if(isSpeedShown) unit.toSpeedLabel() else "/${unit.toDistanceLabel()}"
    )
}

fun Double.toPerformanceChange(unit: DistanceUnit, isSpeedShown: Boolean): String {
    return if(isSpeedShown) {
        val change = roundToOneDecimal()
        "${if(change > 0) "+" else ""}$change ${unit.toSpeedLabel()}"
    } else {
        "${abs(this).roundToInt()} s"
    }
}

fun Int.toFormattedPercentChange(): String {
    return "${if(this > 0) "+" else ""}$this%"
}

private fun Double.roundToOneDecimal(): Double {
    return round(this * 10) / 10
}

package com.kiras.run.presentation.util

import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.domain.goal.GoalMetric
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToLong

data class GoalStepperConfig(
    val baseUnitsPerDisplayUnit: Double,
    val step: Double,
    val min: Double,
    val max: Double,
    val presets: List<Double>,
    val defaultValue: Double
) {
    val minTarget: Long
        get() = toTarget(min)

    val maxTarget: Long
        get() = toTarget(max)

    val defaultTarget: Long
        get() = toTarget(defaultValue)

    val presetTargets: List<Long>
        get() = presets.map(::toTarget)

    fun increment(target: Long): Long {
        val steps = floor(target / baseUnitsPerDisplayUnit / step + STEP_TOLERANCE) + 1
        return toTarget((steps * step).coerceIn(min, max))
    }

    fun decrement(target: Long): Long {
        val steps = ceil(target / baseUnitsPerDisplayUnit / step - STEP_TOLERANCE) - 1
        return toTarget((steps * step).coerceIn(min, max))
    }

    private fun toTarget(value: Double): Long {
        return (value * baseUnitsPerDisplayUnit).roundToLong()
    }

    private companion object {
        const val STEP_TOLERANCE = 0.001
    }
}

fun GoalMetric.toStepperConfig(distanceUnit: DistanceUnit): GoalStepperConfig {
    return when(this) {
        GoalMetric.DISTANCE -> when(distanceUnit) {
            DistanceUnit.KILOMETERS -> GoalStepperConfig(
                baseUnitsPerDisplayUnit = distanceUnit.metersPerUnit,
                step = 0.5,
                min = 0.5,
                max = 100.0,
                presets = listOf(3.0, 5.0, 8.0, 10.0),
                defaultValue = 5.0
            )
            DistanceUnit.MILES -> GoalStepperConfig(
                baseUnitsPerDisplayUnit = distanceUnit.metersPerUnit,
                step = 0.5,
                min = 0.5,
                max = 60.0,
                presets = listOf(2.0, 3.0, 5.0, 6.0),
                defaultValue = 3.0
            )
        }
        GoalMetric.ACTIVE_TIME -> GoalStepperConfig(
            baseUnitsPerDisplayUnit = 60.0,
            step = 5.0,
            min = 5.0,
            max = 300.0,
            presets = listOf(20.0, 30.0, 45.0, 60.0),
            defaultValue = 30.0
        )
    }
}

package com.kiras.run.presentation.util

import androidx.annotation.StringRes
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.presentation.ui.UiText
import com.kiras.run.domain.goal.DayGoalStatus
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.R
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.roundToLong

fun Long.toGoalValueText(metric: GoalMetric, distanceUnit: DistanceUnit): String {
    return when(metric) {
        GoalMetric.DISTANCE -> String.format(
            Locale.getDefault(),
            "%.1f",
            this / distanceUnit.metersPerUnit
        )
        GoalMetric.ACTIVE_TIME -> (this / 60).toString()
    }
}

fun Long.toGoalTargetText(metric: GoalMetric, distanceUnit: DistanceUnit): String {
    return when(metric) {
        GoalMetric.DISTANCE -> {
            val value = this / distanceUnit.metersPerUnit
            if(abs(value - value.roundToLong()) < 0.05) value.roundToLong().toString()
            else toGoalValueText(metric, distanceUnit)
        }
        GoalMetric.ACTIVE_TIME -> (this / 60).toString()
    }
}

fun Long.toGoalPresetText(metric: GoalMetric, distanceUnit: DistanceUnit): UiText {
    return UiText.StringResource(
        id = when(metric) {
            GoalMetric.DISTANCE -> when(distanceUnit) {
                DistanceUnit.KILOMETERS -> R.string.goal_value_km
                DistanceUnit.MILES -> R.string.goal_value_mi
            }
            GoalMetric.ACTIVE_TIME -> R.string.goal_value_min
        },
        args = arrayOf(toGoalTargetText(metric, distanceUnit))
    )
}

fun Long.toRemainingText(metric: GoalMetric, distanceUnit: DistanceUnit): UiText {
    if(this <= 0L) {
        return UiText.StringResource(R.string.goal_reached)
    }
    return when(metric) {
        GoalMetric.DISTANCE -> UiText.StringResource(
            id = when(distanceUnit) {
                DistanceUnit.KILOMETERS -> R.string.km_to_go_today
                DistanceUnit.MILES -> R.string.mi_to_go_today
            },
            args = arrayOf(toGoalValueText(metric, distanceUnit))
        )
        GoalMetric.ACTIVE_TIME -> UiText.StringResource(
            id = R.string.min_to_go_today,
            args = arrayOf(((this + 59) / 60).toString())
        )
    }
}

fun GoalMetric.toUnitText(distanceUnit: DistanceUnit): UiText {
    return UiText.StringResource(
        id = when(this) {
            GoalMetric.DISTANCE -> when(distanceUnit) {
                DistanceUnit.KILOMETERS -> R.string.unit_km
                DistanceUnit.MILES -> R.string.unit_mi
            }
            GoalMetric.ACTIVE_TIME -> R.string.unit_min
        }
    )
}

@StringRes
fun GoalMetric.toLabelRes(): Int {
    return when(this) {
        GoalMetric.DISTANCE -> R.string.distance
        GoalMetric.ACTIVE_TIME -> R.string.active_time
    }
}

@StringRes
fun DayGoalStatus.toDescriptionRes(): Int {
    return when(this) {
        DayGoalStatus.MET -> R.string.day_status_met
        DayGoalStatus.TODAY -> R.string.day_status_today
        DayGoalStatus.MISSED -> R.string.day_status_missed
        DayGoalStatus.UPCOMING -> R.string.day_status_upcoming
        DayGoalStatus.REST -> R.string.day_status_rest
    }
}

fun Float.toPercentText(): String {
    return "${(this * 100).roundToInt()}%"
}

fun DayOfWeek.toNarrowName(): String {
    return getDisplayName(TextStyle.NARROW, Locale.getDefault())
}

fun DayOfWeek.toFullName(): String {
    return getDisplayName(TextStyle.FULL, Locale.getDefault())
}

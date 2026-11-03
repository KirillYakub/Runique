package com.kiras.run.presentation.run_overview.mappers

import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.domain.goal.DailyGoalProgress
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.run_overview.model.DailyGoalUi
import com.kiras.run.presentation.util.toGoalTargetText
import com.kiras.run.presentation.util.toGoalValueText
import com.kiras.run.presentation.util.toPercentText
import com.kiras.run.presentation.util.toRemainingText
import com.kiras.run.presentation.util.toUnitText
import java.time.LocalDate

fun DailyGoalProgress.toDailyGoalUi(
    metric: GoalMetric,
    distanceUnit: DistanceUnit,
    today: LocalDate
): DailyGoalUi {
    val ratio = if(target > 0L) todayValue.toFloat() / target else 0f
    return DailyGoalUi(
        todayValue = todayValue.toGoalValueText(metric, distanceUnit),
        target = target.toGoalTargetText(metric, distanceUnit),
        unit = metric.toUnitText(distanceUnit),
        remaining = (target - todayValue).toRemainingText(metric, distanceUnit),
        progress = ratio.coerceIn(0f, 1f),
        percent = ratio.toPercentText(),
        week = week,
        todayIndex = today.dayOfWeek.ordinal,
        streakDays = streakDays
    )
}

package com.kiras.run.data.mappers

import com.kiras.core.database.entity.DailyGoalEntity
import com.kiras.run.domain.goal.DailyGoal
import com.kiras.run.domain.goal.GoalMetric
import java.time.DayOfWeek

fun DailyGoalEntity.toDailyGoal(): DailyGoal {
    return DailyGoal(
        metric = GoalMetric.valueOf(metric),
        target = target,
        goalDays = goalDays.toDaysOfWeek()
    )
}

fun DailyGoal.toDailyGoalEntity(userId: String): DailyGoalEntity {
    return DailyGoalEntity(
        userId = userId,
        metric = metric.name,
        target = target,
        goalDays = goalDays.toBitmask()
    )
}

private fun Int.toDaysOfWeek(): Set<DayOfWeek> {
    return DayOfWeek.entries
        .filter { day -> this and (1 shl day.ordinal) != 0 }
        .toSet()
}

private fun Set<DayOfWeek>.toBitmask(): Int {
    return fold(0) { mask, day -> mask or (1 shl day.ordinal) }
}

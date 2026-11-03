package com.kiras.run.domain.goal

import com.kiras.core.domain.run.Run
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

object DailyGoalProgressCalculator {

    fun calculate(
        goal: DailyGoal,
        runs: List<Run>,
        today: LocalDate,
        zone: ZoneId
    ): DailyGoalProgress {
        val totalsByDate = runs
            .groupBy { it.dateTimeUtc.withZoneSameInstant(zone).toLocalDate() }
            .mapValues { (_, runsOfDay) ->
                runsOfDay.sumOf { it.goalValue(goal.metric) }
            }
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val week = (0L until DAYS_IN_WEEK).map { offset ->
            getDayStatus(
                date = monday.plusDays(offset),
                goal = goal,
                totalsByDate = totalsByDate,
                today = today
            )
        }

        return DailyGoalProgress(
            todayValue = totalsByDate[today] ?: 0L,
            target = goal.target,
            week = week,
            streakDays = getStreakDays(goal, totalsByDate, today)
        )
    }

    private fun getDayStatus(
        date: LocalDate,
        goal: DailyGoal,
        totalsByDate: Map<LocalDate, Long>,
        today: LocalDate
    ): DayGoalStatus {
        return when {
            date.dayOfWeek !in goal.goalDays -> DayGoalStatus.REST
            isGoalMet(date, goal, totalsByDate) -> DayGoalStatus.MET
            date == today -> DayGoalStatus.TODAY
            date.isBefore(today) -> DayGoalStatus.MISSED
            else -> DayGoalStatus.UPCOMING
        }
    }

    private fun getStreakDays(
        goal: DailyGoal,
        totalsByDate: Map<LocalDate, Long>,
        today: LocalDate
    ): Int {
        val earliestDate = totalsByDate.keys.minOrNull() ?: return 0
        var streak = 0
        var date = today
        while(!date.isBefore(earliestDate)) {
            if(date.dayOfWeek in goal.goalDays) {
                if(isGoalMet(date, goal, totalsByDate)) {
                    streak++
                } else if(date != today) {
                    break
                }
            }
            date = date.minusDays(1)
        }
        return streak
    }

    private fun isGoalMet(
        date: LocalDate,
        goal: DailyGoal,
        totalsByDate: Map<LocalDate, Long>
    ): Boolean {
        return (totalsByDate[date] ?: 0L) >= goal.target
    }

    private fun Run.goalValue(metric: GoalMetric): Long {
        return when(metric) {
            GoalMetric.DISTANCE -> distanceMeters.toLong()
            GoalMetric.ACTIVE_TIME -> duration.inWholeSeconds
        }
    }

    private const val DAYS_IN_WEEK = 7L
}

package com.kiras.statistics.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

object StatisticsCalculator {

    fun calculate(
        entries: List<RunStatsEntry>,
        today: LocalDate,
        zone: ZoneId
    ): StatisticsValues {
        val entriesByDate = entries.groupBy { it.startedAt.atZone(zone).toLocalDate() }
        val monday = today.startOfWeek()
        val totalDistance = entries.sumOf { it.distanceMeters }

        return StatisticsValues(
            runCount = entries.size,
            totalDistanceRun = totalDistance,
            totalTimeRun = entries.fold(Duration.ZERO) { total, entry -> total + entry.duration },
            fastestEverRun = entries.maxOfOrNull { it.maxSpeedKmh } ?: 0.0,
            avgDistancePerRun = if(entries.isEmpty()) 0.0 else totalDistance.toDouble() / entries.size,
            avgPaceSecondsPerKm = entries.paceSecondsPerKm(),
            thisWeek = getWeekSummary(entriesByDate, monday, today),
            weeklyPaceSecondsPerKm = getWeeklyPaces(entriesByDate, monday)
        )
    }

    private fun getWeekSummary(
        entriesByDate: Map<LocalDate, List<RunStatsEntry>>,
        monday: LocalDate,
        today: LocalDate
    ): WeekSummary {
        val dailyDistance = (0L until DAYS_IN_WEEK).map { offset ->
            val date = monday.plusDays(offset)
            if(date.isAfter(today)) 0 else entriesByDate.distanceOn(date)
        }
        val total = dailyDistance.sum()
        val baseline = (0L until DAYS_IN_WEEK)
            .map { offset -> monday.minusWeeks(1).plusDays(offset) }
            .filter { date -> !date.isAfter(today.minusWeeks(1)) }
            .sumOf { date -> entriesByDate.distanceOn(date) }

        return WeekSummary(
            dailyDistanceMeters = dailyDistance,
            totalMeters = total,
            todayIndex = today.dayOfWeek.value - 1,
            changePercent = if(baseline > 0) {
                ((total - baseline) * 100.0 / baseline).roundToInt()
            } else {
                null
            }
        )
    }

    private fun getWeeklyPaces(
        entriesByDate: Map<LocalDate, List<RunStatsEntry>>,
        monday: LocalDate
    ): List<Double?> {
        val entriesByWeek = entriesByDate.entries
            .groupBy(
                keySelector = { (date, _) -> date.startOfWeek() },
                valueTransform = { (_, entriesOfDay) -> entriesOfDay }
            )
            .mapValues { (_, entriesOfWeek) -> entriesOfWeek.flatten() }

        return (TREND_WEEKS - 1L downTo 0L).map { weeksAgo ->
            entriesByWeek[monday.minusWeeks(weeksAgo)]?.paceSecondsPerKm()
        }
    }

    private fun Map<LocalDate, List<RunStatsEntry>>.distanceOn(date: LocalDate): Int {
        return this[date]?.sumOf { it.distanceMeters } ?: 0
    }

    private fun List<RunStatsEntry>.paceSecondsPerKm(): Double? {
        val measurableEntries = filter { it.distanceMeters > 0 && it.duration.isPositive() }
        if(measurableEntries.isEmpty()) {
            return null
        }
        val seconds = measurableEntries.sumOf { it.duration.toDouble(DurationUnit.SECONDS) }
        val kilometers = measurableEntries.sumOf { it.distanceMeters } / METERS_PER_KM
        return seconds / kilometers
    }

    private fun LocalDate.startOfWeek(): LocalDate {
        return with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    }

    private const val DAYS_IN_WEEK = 7L
    private const val TREND_WEEKS = 8L
    private const val METERS_PER_KM = 1000.0
}

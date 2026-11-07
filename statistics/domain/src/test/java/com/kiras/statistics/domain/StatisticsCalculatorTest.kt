package com.kiras.statistics.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class StatisticsCalculatorTest {

    private val thursday = LocalDate.of(2026, 11, 5)
    private val monday = LocalDate.of(2026, 11, 2)

    private fun entry(
        date: LocalDate,
        distanceMeters: Int = 5000,
        duration: Duration = 30.minutes,
        maxSpeedKmh: Double = 12.0,
        time: LocalTime = LocalTime.NOON,
        zone: ZoneId = ZoneOffset.UTC
    ) = RunStatsEntry(
        startedAt = date.atTime(time).atZone(zone).toInstant(),
        distanceMeters = distanceMeters,
        duration = duration,
        maxSpeedKmh = maxSpeedKmh
    )

    private fun calculate(
        vararg entries: RunStatsEntry,
        today: LocalDate = thursday,
        zone: ZoneId = ZoneOffset.UTC
    ) = StatisticsCalculator.calculate(entries.toList(), today, zone)

    @Test
    fun `empty list returns zero totals and no pace`() {
        val values = calculate()

        assertEquals(0, values.runCount)
        assertEquals(0, values.totalDistanceRun)
        assertEquals(Duration.ZERO, values.totalTimeRun)
        assertEquals(0.0, values.fastestEverRun, 0.0)
        assertEquals(0.0, values.avgDistancePerRun, 0.0)
        assertNull(values.avgPaceSecondsPerKm)
        assertEquals(List(7) { 0 }, values.thisWeek.dailyDistanceMeters)
        assertNull(values.thisWeek.changePercent)
        assertEquals(List(8) { null }, values.weeklyPaceSecondsPerKm)
    }

    @Test
    fun `totals averages and fastest run cover all runs`() {
        val values = calculate(
            entry(monday, distanceMeters = 4000, duration = 20.minutes, maxSpeedKmh = 11.0),
            entry(thursday, distanceMeters = 6000, duration = 40.minutes, maxSpeedKmh = 14.8)
        )

        assertEquals(2, values.runCount)
        assertEquals(10000, values.totalDistanceRun)
        assertEquals(60.minutes, values.totalTimeRun)
        assertEquals(14.8, values.fastestEverRun, 0.0)
        assertEquals(5000.0, values.avgDistancePerRun, 0.0)
    }

    @Test
    fun `average pace is total time over total distance rather than a per run average`() {
        val values = calculate(
            entry(monday, distanceMeters = 1000, duration = 10.minutes),
            entry(thursday, distanceMeters = 9000, duration = 45.minutes)
        )

        assertEquals(330.0, values.avgPaceSecondsPerKm!!, 0.001)
    }

    @Test
    fun `run near midnight lands on its local day in a non UTC zone`() {
        val zone = ZoneId.of("Asia/Tokyo")
        val values = calculate(
            entry(thursday, time = LocalTime.of(0, 30), zone = zone),
            zone = zone
        )

        assertEquals(listOf(0, 0, 0, 5000, 0, 0, 0), values.thisWeek.dailyDistanceMeters)
    }

    @Test
    fun `week runs from monday to sunday and excludes the sunday before`() {
        val sunday = monday.plusDays(6)
        val values = calculate(
            entry(monday.minusDays(1), distanceMeters = 9000),
            entry(monday, distanceMeters = 1000),
            entry(sunday, distanceMeters = 2000),
            today = sunday
        )

        assertEquals(listOf(1000, 0, 0, 0, 0, 0, 2000), values.thisWeek.dailyDistanceMeters)
        assertEquals(3000, values.thisWeek.totalMeters)
        assertEquals(6, values.thisWeek.todayIndex)
    }

    @Test
    fun `today index is zero based from monday`() {
        assertEquals(3, calculate(today = thursday).thisWeek.todayIndex)
        assertEquals(0, calculate(today = monday).thisWeek.todayIndex)
    }

    @Test
    fun `days after today stay empty`() {
        val values = calculate(entry(thursday.plusDays(2)))

        assertEquals(List(7) { 0 }, values.thisWeek.dailyDistanceMeters)
        assertEquals(0, values.thisWeek.totalMeters)
    }

    @Test
    fun `change compares the same days and ignores the later days of last week`() {
        val values = calculate(
            entry(thursday, distanceMeters = 6000),
            entry(thursday.minusWeeks(1), distanceMeters = 5000),
            entry(thursday.minusWeeks(1).plusDays(1), distanceMeters = 50000)
        )

        assertEquals(20, values.thisWeek.changePercent)
    }

    @Test
    fun `change can be negative`() {
        val values = calculate(
            entry(thursday, distanceMeters = 4000),
            entry(thursday.minusWeeks(1), distanceMeters = 5000)
        )

        assertEquals(-20, values.thisWeek.changePercent)
    }

    @Test
    fun `change is null when last week has no distance in the comparison span`() {
        val values = calculate(
            entry(thursday, distanceMeters = 6000),
            entry(thursday.minusWeeks(1).plusDays(1), distanceMeters = 5000)
        )

        assertNull(values.thisWeek.changePercent)
    }

    @Test
    fun `trend has eight slots oldest first with null for empty weeks`() {
        val values = calculate(
            entry(monday, distanceMeters = 1000, duration = 5.minutes),
            entry(monday.minusWeeks(2), distanceMeters = 1000, duration = 6.minutes),
            entry(monday.minusWeeks(7), distanceMeters = 1000, duration = 7.minutes)
        )

        assertEquals(
            listOf(420.0, null, null, null, null, 360.0, null, 300.0),
            values.weeklyPaceSecondsPerKm
        )
    }

    @Test
    fun `trend excludes runs older than eight weeks`() {
        val values = calculate(entry(monday.minusWeeks(8)))

        assertEquals(List(8) { null }, values.weeklyPaceSecondsPerKm)
    }

    @Test
    fun `weekly pace is time over distance for that week`() {
        val values = calculate(
            entry(monday, distanceMeters = 1000, duration = 10.minutes),
            entry(thursday, distanceMeters = 9000, duration = 45.minutes)
        )

        assertEquals(330.0, values.weeklyPaceSecondsPerKm.last()!!, 0.001)
    }

    @Test
    fun `zero distance runs count towards totals but not pace`() {
        val values = calculate(
            entry(monday, distanceMeters = 0, duration = 10.minutes),
            entry(thursday, distanceMeters = 1000, duration = 5.minutes)
        )

        assertEquals(2, values.runCount)
        assertEquals(15.minutes, values.totalTimeRun)
        assertEquals(300.0, values.avgPaceSecondsPerKm!!, 0.001)
    }

    @Test
    fun `zero duration runs are left out of pace`() {
        val values = calculate(
            entry(monday, distanceMeters = 1000, duration = Duration.ZERO),
            entry(thursday, distanceMeters = 1000, duration = 5.minutes)
        )

        assertEquals(300.0, values.avgPaceSecondsPerKm!!, 0.001)
    }

    @Test
    fun `only zero distance runs give no pace`() {
        val values = calculate(entry(thursday, distanceMeters = 0))

        assertNull(values.avgPaceSecondsPerKm)
        assertEquals(List(8) { null }, values.weeklyPaceSecondsPerKm)
    }
}

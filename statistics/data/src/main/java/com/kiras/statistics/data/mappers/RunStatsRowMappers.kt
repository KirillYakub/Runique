package com.kiras.statistics.data.mappers

import com.kiras.core.database.entity.RunStatsRow
import com.kiras.statistics.domain.RunStatsEntry
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

fun RunStatsRow.toRunStatsEntry(): RunStatsEntry {
    return RunStatsEntry(
        startedAt = Instant.parse(dateTimeUtc),
        distanceMeters = distanceMeters,
        duration = durationMillis.milliseconds,
        maxSpeedKmh = maxSpeedKm
    )
}

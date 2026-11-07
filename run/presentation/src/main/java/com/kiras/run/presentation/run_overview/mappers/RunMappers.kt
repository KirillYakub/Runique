package com.kiras.run.presentation.run_overview.mappers

import com.kiras.core.domain.run.Run
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.domain.settings.UserSettings
import com.kiras.core.presentation.ui.formatted
import com.kiras.core.presentation.ui.toFormattedDistance
import com.kiras.core.presentation.ui.toFormattedMeters
import com.kiras.core.presentation.ui.toFormattedPace
import com.kiras.core.presentation.ui.toFormattedPreciseDistance
import com.kiras.core.presentation.ui.toFormattedSpeed
import com.kiras.run.presentation.run_overview.model.RunUi
import com.kiras.run.presentation.util.toRunDateTitle
import com.kiras.run.presentation.util.toRunStartTime
import com.kiras.run.presentation.util.toShortDurationText
import java.time.ZoneId

fun Run.toRunUi(settings: UserSettings): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val averagePace = duration.toFormattedPace(distanceMeters, settings.distanceUnit)
    val avgSpeed = avgSpeedKmh.toFormattedSpeed(settings.distanceUnit)

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        shortDuration = duration.toShortDurationText(),
        dateTitle = dateTimeInLocalTime.toRunDateTitle(),
        startTime = dateTimeInLocalTime.toRunStartTime(),
        localDateTime = dateTimeInLocalTime,
        distance = distanceMeters.toFormattedDistance(settings.distanceUnit),
        preciseDistance = distanceMeters.toFormattedPreciseDistance(settings.distanceUnit),
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeedKmh.toFormattedSpeed(settings.distanceUnit),
        pace = when(settings.paceFormat) {
            PaceFormat.PACE -> averagePace
            PaceFormat.SPEED -> null
        },
        averagePace = averagePace,
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl,
        splits = splits.toRunSplitsUi(
            unit = settings.distanceUnit,
            paceFormat = settings.paceFormat,
            averageText = when(settings.paceFormat) {
                PaceFormat.PACE -> averagePace
                PaceFormat.SPEED -> avgSpeed
            }
        )
    )
}

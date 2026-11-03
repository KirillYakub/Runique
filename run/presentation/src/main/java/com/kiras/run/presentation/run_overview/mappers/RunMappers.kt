package com.kiras.run.presentation.run_overview.mappers

import com.kiras.core.domain.run.Run
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.domain.settings.UserSettings
import com.kiras.core.presentation.ui.formatted
import com.kiras.core.presentation.ui.toFormattedDistance
import com.kiras.core.presentation.ui.toFormattedMeters
import com.kiras.core.presentation.ui.toFormattedPace
import com.kiras.core.presentation.ui.toFormattedSpeed
import com.kiras.run.presentation.run_overview.model.RunUi
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Run.toRunUi(settings: UserSettings): RunUi {
    val dateTimeInLocalTime = dateTimeUtc
        .withZoneSameInstant(ZoneId.systemDefault())
    val formattedDateTime = DateTimeFormatter
        .ofPattern("MMM dd, yyyy - hh:mma")
        .format(dateTimeInLocalTime)

    return RunUi(
        id = id!!,
        duration = duration.formatted(),
        dateTime = formattedDateTime,
        localDateTime = dateTimeInLocalTime,
        distance = distanceMeters.toFormattedDistance(settings.distanceUnit),
        avgSpeed = avgSpeedKmh.toFormattedSpeed(settings.distanceUnit),
        maxSpeed = maxSpeedKmh.toFormattedSpeed(settings.distanceUnit),
        pace = when(settings.paceFormat) {
            PaceFormat.PACE -> duration.toFormattedPace(distanceMeters, settings.distanceUnit)
            PaceFormat.SPEED -> null
        },
        totalElevation = totalElevationMeters.toFormattedMeters(),
        mapPictureUrl = mapPictureUrl
    )
}

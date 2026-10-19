package com.kiras.core.database.mappers

import com.kiras.core.database.entity.RunEntity
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.run.Run
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunEntity.toRun(): Run {
    return Run(
        id = id,
        duration = durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = distanceMeters,
        location = Location(
            lat = latitude,
            long = longitude
        ),
        maxSpeedKmh = maxSpeedKm,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}

fun Run.toRunEntity(userId: String): RunEntity {
    return RunEntity(
        id = id ?: ObjectId().toHexString(),
        userId = userId,
        durationMillis = duration.inWholeMilliseconds,
        dateTimeUtc = dateTimeUtc.toInstant().toString(),
        latitude = location.lat,
        longitude = location.long,
        distanceMeters = distanceMeters,
        avgSpeedKmh = avgSpeedKmh,
        maxSpeedKm = maxSpeedKmh,
        totalElevationMeters = totalElevationMeters,
        mapPictureUrl = mapPictureUrl
    )
}
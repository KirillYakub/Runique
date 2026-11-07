package com.kiras.core.database.mappers

import com.kiras.core.database.entity.RunEntity
import com.kiras.core.database.entity.RunSplitEntity
import com.kiras.core.database.entity.RunWithSplits
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.run.RecordedSplit
import com.kiras.core.domain.run.Run
import com.kiras.core.domain.settings.DistanceUnit
import org.bson.types.ObjectId
import java.time.Instant
import java.time.ZoneId
import kotlin.time.Duration.Companion.milliseconds

fun RunWithSplits.toRun(): Run {
    return Run(
        id = run.id,
        duration = run.durationMillis.milliseconds,
        dateTimeUtc = Instant.parse(run.dateTimeUtc).atZone(ZoneId.of("UTC")),
        distanceMeters = run.distanceMeters,
        location = Location(
            lat = run.latitude,
            long = run.longitude
        ),
        maxSpeedKmh = run.maxSpeedKm,
        totalElevationMeters = run.totalElevationMeters,
        mapPictureUrl = run.mapPictureUrl,
        splits = splits
            .map { it.toRecordedSplit() }
            .sortedBy { it.number }
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

fun RunSplitEntity.toRecordedSplit(): RecordedSplit {
    return RecordedSplit(
        unit = DistanceUnit.valueOf(distanceUnit),
        number = number,
        distanceMeters = distanceMeters,
        duration = durationMillis.milliseconds
    )
}

fun RecordedSplit.toRunSplitEntity(runId: String): RunSplitEntity {
    return RunSplitEntity(
        runId = runId,
        distanceUnit = unit.name,
        number = number,
        distanceMeters = distanceMeters,
        durationMillis = duration.inWholeMilliseconds
    )
}

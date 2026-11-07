package com.kiras.run.domain

import com.kiras.core.domain.location.Location
import com.kiras.core.domain.location.LocationTimestamp
import com.kiras.core.domain.run.RecordedSplit
import com.kiras.core.domain.settings.DistanceUnit
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.DurationUnit

object LocationDataCalculator {
    fun getTotalDistanceMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations
            .sumOf { timestampsPerLine ->
                timestampsPerLine.zipWithNext { location1, location2 ->
                    location1.location.location.distanceTo(location2.location.location)
                }.sum().roundToInt()
            }
    }

    fun getTotalElevationMeters(locations: List<List<LocationTimestamp>>): Int {
        return locations.sumOf { locationsSet ->
            locationsSet.zipWithNext { location1, location2 ->
                val altitude1 = location1.location.altitude
                val altitude2 = location2.location.altitude
                (altitude2 - altitude1).coerceAtLeast(0.0)
            }.sum().roundToInt()
        }
    }

    fun getMaxSpeedKmh(locations: List<List<LocationTimestamp>>): Double {
        return locations.maxOfOrNull { locationsSet ->
            locationsSet.zipWithNext { location1, location2 ->
                val distance = location1.location.location.distanceTo(
                    location2.location.location
                )
                val hoursDifference = (location2.durationTimestamp - location1.durationTimestamp)
                    .toDouble(DurationUnit.HOURS)
                if(hoursDifference == 0.0)
                    0.0
                else
                    (distance / 1000) / hoursDifference
            }.maxOrNull() ?: 0.0
        } ?: 0.0
    }

    fun getSplits(
        locations: List<List<LocationTimestamp>>,
        metersPerSplit: Double
    ): List<RunSplit> {
        var lastSplitTimestamp = locations
            .firstOrNull { it.isNotEmpty() }
            ?.first()
            ?.durationTimestamp
            ?: return emptyList()
        var metersSinceLastSplit = 0.0
        val splits = mutableListOf<RunSplit>()

        locations.forEach { timestampsPerLine ->
            timestampsPerLine.zipWithNext().forEach { (start, end) ->
                val startLocation = start.location.location
                val endLocation = end.location.location
                val pairMeters = startLocation.distanceTo(endLocation).toDouble()
                var coveredMeters = 0.0

                while(metersSinceLastSplit + pairMeters - coveredMeters >= metersPerSplit) {
                    coveredMeters += metersPerSplit - metersSinceLastSplit
                    val fraction = coveredMeters / pairMeters
                    val splitTimestamp = start.durationTimestamp +
                            (end.durationTimestamp - start.durationTimestamp) * fraction

                    splits += RunSplit(
                        number = splits.size + 1,
                        duration = splitTimestamp - lastSplitTimestamp,
                        endLocation = Location(
                            lat = startLocation.lat + (endLocation.lat - startLocation.lat) * fraction,
                            long = startLocation.long + (endLocation.long - startLocation.long) * fraction
                        )
                    )
                    lastSplitTimestamp = splitTimestamp
                    metersSinceLastSplit = 0.0
                }
                metersSinceLastSplit += pairMeters - coveredMeters
            }
        }
        return splits
    }

    fun getRecordedSplits(locations: List<List<LocationTimestamp>>): List<RecordedSplit> {
        val firstTimestamp = locations
            .firstOrNull { it.isNotEmpty() }
            ?.first()
            ?.durationTimestamp
            ?: return emptyList()
        val lastTimestamp = locations.last { it.isNotEmpty() }.last().durationTimestamp
        val routeMeters = locations.sumOf { timestampsPerLine ->
            timestampsPerLine.zipWithNext { location1, location2 ->
                location1.location.location.distanceTo(location2.location.location).toDouble()
            }.sum()
        }

        return DistanceUnit.entries.flatMap { unit ->
            val splits = getSplits(locations, unit.metersPerUnit)
            val partialMeters = routeMeters - splits.size * unit.metersPerUnit
            val completedDuration = splits.fold(Duration.ZERO) { total, split ->
                total + split.duration
            }

            buildList {
                splits.forEach { split ->
                    add(
                        RecordedSplit(
                            unit = unit,
                            number = split.number,
                            distanceMeters = unit.metersPerUnit,
                            duration = split.duration
                        )
                    )
                }
                if(partialMeters >= MIN_PARTIAL_SPLIT_METERS) {
                    add(
                        RecordedSplit(
                            unit = unit,
                            number = splits.size + 1,
                            distanceMeters = partialMeters,
                            duration = lastTimestamp - firstTimestamp - completedDuration
                        )
                    )
                }
            }
        }
    }

    private const val MIN_PARTIAL_SPLIT_METERS = 10.0
}
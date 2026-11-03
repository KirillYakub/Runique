package com.kiras.core.domain.settings

data class UserSettings(
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val paceFormat: PaceFormat = PaceFormat.PACE,
    val countdown: CountdownDuration = CountdownDuration.THREE,
    val keepScreenOn: Boolean = false,
    val gpsAccuracy: GpsAccuracy = GpsAccuracy.HIGH,
    val theme: AppTheme = AppTheme.SYSTEM
)

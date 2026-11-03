package com.kiras.core.database.mappers

import com.kiras.core.database.entity.UserSettingsEntity
import com.kiras.core.domain.settings.AppTheme
import com.kiras.core.domain.settings.CountdownDuration
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.GpsAccuracy
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.domain.settings.UserSettings

fun UserSettingsEntity.toUserSettings(): UserSettings {
    val defaults = UserSettings()
    return UserSettings(
        distanceUnit = DistanceUnit.entries.firstOrNull { it.name == distanceUnit }
            ?: defaults.distanceUnit,
        paceFormat = PaceFormat.entries.firstOrNull { it.name == paceFormat }
            ?: defaults.paceFormat,
        countdown = CountdownDuration.entries.firstOrNull { it.seconds == countdownSeconds }
            ?: defaults.countdown,
        keepScreenOn = keepScreenOn,
        gpsAccuracy = GpsAccuracy.entries.firstOrNull { it.name == gpsAccuracy }
            ?: defaults.gpsAccuracy,
        theme = AppTheme.entries.firstOrNull { it.name == theme }
            ?: defaults.theme
    )
}

fun UserSettings.toUserSettingsEntity(userId: String): UserSettingsEntity {
    return UserSettingsEntity(
        userId = userId,
        distanceUnit = distanceUnit.name,
        paceFormat = paceFormat.name,
        countdownSeconds = countdown.seconds,
        keepScreenOn = keepScreenOn,
        gpsAccuracy = gpsAccuracy.name,
        theme = theme.name
    )
}

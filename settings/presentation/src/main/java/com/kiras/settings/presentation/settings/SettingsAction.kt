package com.kiras.settings.presentation.settings

import com.kiras.core.domain.settings.AppTheme
import com.kiras.core.domain.settings.CountdownDuration
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.GpsAccuracy
import com.kiras.core.domain.settings.PaceFormat

sealed interface SettingsAction {
    data class OnDistanceUnitChange(val unit: DistanceUnit): SettingsAction
    data class OnPaceFormatChange(val format: PaceFormat): SettingsAction
    data class OnKeepScreenOnToggle(val isEnabled: Boolean): SettingsAction
    data class OnThemeChange(val theme: AppTheme): SettingsAction
    data object OnCountdownClick: SettingsAction
    data object OnGpsAccuracyClick: SettingsAction
    data class OnCountdownSelect(val countdown: CountdownDuration): SettingsAction
    data class OnGpsAccuracySelect(val accuracy: GpsAccuracy): SettingsAction
    data object OnPickerDismiss: SettingsAction
    data object OnHomeClick: SettingsAction
    data object OnStatisticsClick: SettingsAction
}

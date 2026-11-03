package com.kiras.settings.presentation.settings

import com.kiras.core.domain.settings.UserSettings

data class SettingsState(
    val settings: UserSettings = UserSettings(),
    val openPicker: SettingsPicker? = null
)

package com.kiras.settings.presentation.settings

import com.kiras.core.presentation.ui.UiText

sealed interface SettingsEvent {
    data class Error(val error: UiText): SettingsEvent
}

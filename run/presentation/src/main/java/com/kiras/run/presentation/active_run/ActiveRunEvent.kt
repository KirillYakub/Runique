package com.kiras.run.presentation.active_run

import com.kiras.core.presentation.ui.UiText

sealed interface ActiveRunEvent {
    data class Error(val error: UiText): ActiveRunEvent
    data object TrySaveRunWithoutMove : ActiveRunEvent
    data object RunSaved: ActiveRunEvent
}
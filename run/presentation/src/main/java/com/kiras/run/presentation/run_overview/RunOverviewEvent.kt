package com.kiras.run.presentation.run_overview

import com.kiras.core.presentation.ui.UiText

sealed interface RunOverviewEvent {
    data class Error(val error: UiText): RunOverviewEvent
}

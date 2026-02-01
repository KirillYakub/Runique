package com.kiras.run.presentation.run_overview

import com.kiras.run.presentation.run_overview.model.RunUi

sealed interface RunOverviewActions {
    data object OnStartClick: RunOverviewActions
    data object OnLogOutClick: RunOverviewActions
    data object OnAnalyticsClick: RunOverviewActions
    data class DeleteRun(val runUi: RunUi): RunOverviewActions
}
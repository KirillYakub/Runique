package com.kiras.run.presentation.run_overview

import com.kiras.run.presentation.run_overview.model.RunUi

sealed interface RunOverviewAction {
    data object OnStartClick: RunOverviewAction
    data object OnLogOutClick: RunOverviewAction
    data object OnStatisticsClick: RunOverviewAction
    data object OnAccountClick: RunOverviewAction
    data object OnAccountMenuDismiss: RunOverviewAction
    data object OnSeeAllRunsClick: RunOverviewAction
    data object OnAllRunsBackClick: RunOverviewAction
    data class OnDeleteRunClick(val runUi: RunUi): RunOverviewAction
}

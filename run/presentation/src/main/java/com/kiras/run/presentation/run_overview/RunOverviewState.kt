package com.kiras.run.presentation.run_overview

import com.kiras.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList(),
    val lastRun: RunUi? = null,
    val username: String = "",
    val isAccountMenuOpen: Boolean = false,
    val isAllRunsOpen: Boolean = false
)

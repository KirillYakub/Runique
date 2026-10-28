package com.kiras.run.presentation.run_overview

import com.kiras.run.presentation.run_overview.model.RunUi

data class RunOverviewState(
    val runs: List<RunUi> = emptyList(),
    val username: String = "",
    val isAccountMenuOpen: Boolean = false
)

package com.kiras.statistics.presentation.dashboard

sealed interface StatisticsDashboardAction {
    data object OnHomeClick: StatisticsDashboardAction
}

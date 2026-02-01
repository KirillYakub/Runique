package com.kiras.analytics.presentation

sealed interface AnalyticsAction {
    data object onBackClick: AnalyticsAction
}
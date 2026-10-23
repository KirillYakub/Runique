package com.kiras.analytics.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.analytics.domain.AnalyticsRepository
import com.kiras.analytics.presentation.dashboard.mappers.toAnalyticsDashboardState
import kotlinx.coroutines.launch

class AnalyticsDashboardViewModel(
    analyticsRepository: AnalyticsRepository
) : ViewModel() {

    var state by mutableStateOf<AnalyticsDashboardState?>(null)
        private set

    init {
        viewModelScope.launch {
            state = analyticsRepository.getAnalyticsValues().toAnalyticsDashboardState()
        }
    }
}

package com.kiras.statistics.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.statistics.domain.StatisticsRepository
import com.kiras.statistics.presentation.dashboard.mappers.toStatisticsDashboardState
import kotlinx.coroutines.launch

class StatisticsDashboardViewModel(
    statisticsRepository: StatisticsRepository
) : ViewModel() {

    var state by mutableStateOf<StatisticsDashboardState?>(null)
        private set

    init {
        viewModelScope.launch {
            state = statisticsRepository.getStatisticsValues().toStatisticsDashboardState()
        }
    }
}

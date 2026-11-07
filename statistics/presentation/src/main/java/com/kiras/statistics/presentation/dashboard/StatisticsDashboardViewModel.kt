package com.kiras.statistics.presentation.dashboard

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kiras.core.domain.settings.SettingsRepository
import com.kiras.statistics.domain.StatisticsCalculator
import com.kiras.statistics.domain.StatisticsRepository
import com.kiras.statistics.presentation.dashboard.mappers.toStatisticsDashboardState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import java.time.ZoneId

class StatisticsDashboardViewModel(
    statisticsRepository: StatisticsRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    var state by mutableStateOf(StatisticsDashboardState())
        private set

    init {
        combine(
            statisticsRepository.getRunEntries(),
            settingsRepository.getSettings()
        ) { entries, settings ->
            val zone = ZoneId.systemDefault()
            StatisticsCalculator
                .calculate(entries, LocalDate.now(zone), zone)
                .toStatisticsDashboardState(settings)
        }
            .flowOn(Dispatchers.Default)
            .onEach { state = it }
            .launchIn(viewModelScope)
    }
}

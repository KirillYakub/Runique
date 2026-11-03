package com.kiras.statistics.presentation.di

import com.kiras.statistics.presentation.dashboard.StatisticsDashboardViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val statisticsPresentationModule = module {
    viewModelOf(::StatisticsDashboardViewModel)
}

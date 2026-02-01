package com.kiras.run.presentation.run_overview.di

import com.kiras.run.domain.RunningTracker
import com.kiras.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val runViewModelModule = module {
    singleOf(::RunningTracker)
    viewModelOf(::RunOverviewViewModel)
}
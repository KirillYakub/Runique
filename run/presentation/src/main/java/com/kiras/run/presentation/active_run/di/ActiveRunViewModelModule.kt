package com.kiras.run.presentation.active_run.di

import com.kiras.run.presentation.active_run.ActiveRunViewModel
import com.kiras.run.presentation.run_overview.RunOverviewViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val activeRunViewModelModule = module {
    viewModelOf(::ActiveRunViewModel)
}
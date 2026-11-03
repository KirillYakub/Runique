package com.kiras.run.data.di

import com.kiras.run.data.DailyGoalRepositoryImpl
import com.kiras.run.domain.goal.DailyGoalRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    singleOf(::DailyGoalRepositoryImpl).bind<DailyGoalRepository>()
}

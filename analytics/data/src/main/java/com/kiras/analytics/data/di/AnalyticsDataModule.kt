package com.kiras.analytics.data.di

import com.kiras.analytics.data.AnalyticsRepositoryImpl
import com.kiras.analytics.domain.AnalyticsRepository
import com.kiras.core.database.RunDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsDataModule = module {
    singleOf(::AnalyticsRepositoryImpl).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}
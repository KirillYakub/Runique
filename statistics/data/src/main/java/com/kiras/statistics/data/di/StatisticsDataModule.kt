package com.kiras.statistics.data.di

import com.kiras.core.database.RunDatabase
import com.kiras.statistics.data.StatisticsRepositoryImpl
import com.kiras.statistics.domain.StatisticsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val statisticsDataModule = module {
    singleOf(::StatisticsRepositoryImpl).bind<StatisticsRepository>()
    single {
        get<RunDatabase>().statisticsDao
    }
}

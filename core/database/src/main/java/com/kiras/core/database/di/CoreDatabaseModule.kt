package com.kiras.core.database.di

import androidx.room.Room
import com.kiras.core.database.RoomLocalRunDataSource
import com.kiras.core.database.RunDatabase
import com.kiras.core.database.SettingsRepositoryImpl
import com.kiras.core.database.UserRepositoryImpl
import com.kiras.core.domain.run.LocalRunDataSource
import com.kiras.core.domain.settings.SettingsRepository
import com.kiras.core.domain.user.UserRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDatabaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run_db"
        ).build()
    }
    single {
        get<RunDatabase>().runDao
    }
    single {
        get<RunDatabase>().userDao
    }
    single {
        get<RunDatabase>().dailyGoalDao
    }
    single {
        get<RunDatabase>().userSettingsDao
    }

    single {
        RoomLocalRunDataSource(get())
    } bind LocalRunDataSource::class

    singleOf(::UserRepositoryImpl).bind<UserRepository>()
    singleOf(::SettingsRepositoryImpl).bind<SettingsRepository>()
}

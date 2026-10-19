package com.kiras.core.database.di

import androidx.room.Room
import com.kiras.core.database.RoomLocalRunDataSource
import com.kiras.core.database.RunDatabase
import com.kiras.core.domain.run.LocalRunDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.bind
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            RunDatabase::class.java,
            "run_db"
        )
            // v1 runs had no owner and cannot be attributed to a local user, so they are dropped.
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single {
        get<RunDatabase>().runDao
    }
    single {
        get<RunDatabase>().userDao
    }

    single {
        RoomLocalRunDataSource(get())
    } bind LocalRunDataSource::class
}

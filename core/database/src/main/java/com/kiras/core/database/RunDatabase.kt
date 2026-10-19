package com.kiras.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiras.core.database.dao.AnalyticsDao
import com.kiras.core.database.dao.RunDao
import com.kiras.core.database.dao.UserDao
import com.kiras.core.database.entity.RunEntity
import com.kiras.core.database.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        RunEntity::class
    ],
    version = 2
)
abstract class RunDatabase: RoomDatabase() {
    abstract val runDao: RunDao
    abstract val userDao: UserDao
    abstract val analyticsDao: AnalyticsDao
}

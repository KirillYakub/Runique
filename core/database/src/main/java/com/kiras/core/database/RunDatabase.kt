package com.kiras.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiras.core.database.dao.DailyGoalDao
import com.kiras.core.database.dao.StatisticsDao
import com.kiras.core.database.dao.RunDao
import com.kiras.core.database.dao.UserDao
import com.kiras.core.database.dao.UserSettingsDao
import com.kiras.core.database.entity.DailyGoalEntity
import com.kiras.core.database.entity.RunEntity
import com.kiras.core.database.entity.RunSplitEntity
import com.kiras.core.database.entity.UserEntity
import com.kiras.core.database.entity.UserSettingsEntity

@Database(
    entities = [
        UserEntity::class,
        RunEntity::class,
        DailyGoalEntity::class,
        UserSettingsEntity::class,
        RunSplitEntity::class
    ],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6)
    ]
)
abstract class RunDatabase: RoomDatabase() {
    abstract val runDao: RunDao
    abstract val userDao: UserDao
    abstract val statisticsDao: StatisticsDao
    abstract val dailyGoalDao: DailyGoalDao
    abstract val userSettingsDao: UserSettingsDao
}

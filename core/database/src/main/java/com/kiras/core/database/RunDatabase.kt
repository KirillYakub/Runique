package com.kiras.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kiras.core.database.dao.AnalyticsDao
import com.kiras.core.database.dao.RunDao
import com.kiras.core.database.dao.RunPendingSyncDao
import com.kiras.core.database.entity.DeletedRunSyncEntity
import com.kiras.core.database.entity.RunEntity
import com.kiras.core.database.entity.RunPendingSyncEntity

@Database(
    entities = [
        RunEntity::class,
        RunPendingSyncEntity::class,
        DeletedRunSyncEntity::class
    ],
    version = 1
)
abstract class RunDatabase: RoomDatabase() {
    abstract val runDao: RunDao
    abstract val runPendingSyncDao: RunPendingSyncDao
    abstract val analyticsDao: AnalyticsDao
}
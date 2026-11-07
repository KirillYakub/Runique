package com.kiras.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.kiras.core.database.entity.RunStatsRow
import kotlinx.coroutines.flow.Flow

@Dao
interface StatisticsDao {

    @Query("SELECT dateTimeUtc, distanceMeters, durationMillis, maxSpeedKm FROM runentity WHERE userId = :userId")
    fun getRunStats(userId: String): Flow<List<RunStatsRow>>
}

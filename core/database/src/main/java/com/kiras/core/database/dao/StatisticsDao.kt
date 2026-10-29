package com.kiras.core.database.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface StatisticsDao {

    @Query("SELECT SUM(distanceMeters) FROM runentity WHERE userId = :userId")
    suspend fun getTotalDistanceRun(userId: String): Int?

    @Query("SELECT SUM(durationMillis) FROM runentity WHERE userId = :userId")
    suspend fun getTotalTimeRun(userId: String): Long?

    @Query("SELECT MAX(maxSpeedKm) FROM runentity WHERE userId = :userId")
    suspend fun getMaxRunSpeed(userId: String): Double?

    @Query("SELECT AVG(distanceMeters) FROM runentity WHERE userId = :userId")
    suspend fun getAvgDistancePerRun(userId: String): Double?

    @Query("SELECT AVG((durationMillis / 60000.0) / (distanceMeters / 1000.0)) FROM runentity WHERE userId = :userId")
    suspend fun getAvgPacePerRun(userId: String): Double?
}

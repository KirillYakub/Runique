package com.kiras.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kiras.core.database.entity.DailyGoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyGoalDao {

    @Upsert
    suspend fun upsertDailyGoal(dailyGoal: DailyGoalEntity)

    @Query("SELECT * FROM daily_goals WHERE userId = :userId LIMIT 1")
    fun getDailyGoal(userId: String): Flow<DailyGoalEntity?>
}

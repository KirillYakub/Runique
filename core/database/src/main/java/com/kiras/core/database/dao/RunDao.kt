package com.kiras.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kiras.core.database.entity.RunEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Query("SELECT * FROM runentity WHERE userId = :userId ORDER BY dateTimeUtc DESC")
    fun getRunsForUser(userId: String): Flow<List<RunEntity>>

    @Query("DELETE FROM runentity WHERE id = :id AND userId = :userId")
    suspend fun deleteRun(id: String, userId: String)

    @Query("DELETE FROM runentity WHERE userId = :userId")
    suspend fun deleteAllRunsForUser(userId: String)
}

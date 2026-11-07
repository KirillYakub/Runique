package com.kiras.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.kiras.core.database.entity.RunEntity
import com.kiras.core.database.entity.RunSplitEntity
import com.kiras.core.database.entity.RunWithSplits
import kotlinx.coroutines.flow.Flow

@Dao
interface RunDao {

    @Upsert
    suspend fun upsertRun(run: RunEntity)

    @Upsert
    suspend fun upsertSplits(splits: List<RunSplitEntity>)

    @Transaction
    suspend fun upsertRunWithSplits(run: RunEntity, splits: List<RunSplitEntity>) {
        upsertRun(run)
        upsertSplits(splits)
    }

    @Transaction
    @Query("SELECT * FROM runentity WHERE userId = :userId ORDER BY dateTimeUtc DESC")
    fun getRunsForUser(userId: String): Flow<List<RunWithSplits>>

    @Query("DELETE FROM runentity WHERE id = :id AND userId = :userId")
    suspend fun deleteRun(id: String, userId: String)

    @Query("DELETE FROM runentity WHERE userId = :userId")
    suspend fun deleteAllRunsForUser(userId: String)
}

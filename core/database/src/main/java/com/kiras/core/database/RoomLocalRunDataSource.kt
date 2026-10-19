package com.kiras.core.database

import android.database.sqlite.SQLiteFullException
import com.kiras.core.database.dao.RunDao
import com.kiras.core.database.mappers.toRun
import com.kiras.core.database.mappers.toRunEntity
import com.kiras.core.domain.run.LocalRunDataSource
import com.kiras.core.domain.run.Run
import com.kiras.core.domain.run.RunId
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLocalRunDataSource(
    private val runDao: RunDao
) : LocalRunDataSource {

    override fun getRuns(userId: String): Flow<List<Run>> {
        return runDao.getRunsForUser(userId).map { runs ->
            runs.map { it.toRun() }
        }
    }

    override suspend fun upsertRun(run: Run, userId: String): Result<RunId, DataError.Local> {
        return try {
            val entity = run.toRunEntity(userId)
            runDao.upsertRun(entity)
            Result.Success(entity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISC_FULL)
        }
    }

    override suspend fun deleteRun(id: RunId, userId: String) {
        runDao.deleteRun(id, userId)
    }

    override suspend fun deleteAllRuns(userId: String) {
        runDao.deleteAllRunsForUser(userId)
    }
}

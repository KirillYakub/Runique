package com.kiras.core.database

import android.database.sqlite.SQLiteFullException
import android.util.Log
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

    override fun getRuns(): Flow<List<Run>> {
        return runDao.getRuns().map { runs ->
            runs.map { it.toRun() }
        }
    }

    override suspend fun upsertRun(run: Run): Result<RunId, DataError.Local> {
        return try {
            val entity = run.toRunEntity()
            runDao.upsertRun(entity)
            Result.Success(entity.id)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISC_FULL)
        }
    }

    override suspend fun upsertRuns(runs: List<Run>): Result<List<RunId>, DataError.Local> {
        return try {
            val entities = runs.map { it.toRunEntity() }
            runDao.upsertRuns(entities)
            Result.Success(entities.map { it.id })
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISC_FULL)
        }
    }

    override suspend fun deleteRun(id: RunId) {
        runDao.deleteRun(id)
    }

    override suspend fun deleteAllRuns() {
        runDao.deleteAllRuns()
    }
}
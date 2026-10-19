package com.kiras.core.domain.run

import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.Result
import kotlinx.coroutines.flow.Flow

typealias RunId = String

interface LocalRunDataSource {
    fun getRuns(userId: String): Flow<List<Run>>
    suspend fun upsertRun(run: Run, userId: String): Result<RunId, DataError.Local>
    suspend fun deleteRun(id: RunId, userId: String)
    suspend fun deleteAllRuns(userId: String)
}

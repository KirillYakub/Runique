package com.kiras.core.domain.run

import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface RunRepository {
    fun getRuns(): Flow<List<Run>>
    suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError.Local>
    suspend fun deleteRun(id: RunId)
    suspend fun deleteAllRuns()
}

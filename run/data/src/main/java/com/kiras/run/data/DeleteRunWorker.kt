package com.kiras.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kiras.core.database.dao.RunPendingSyncDao
import com.kiras.core.domain.run.RemoteRunDataSource
import com.kiras.core.domain.util.DataError

class DeleteRunWorker(
    context: Context,
    private val workerParams: WorkerParameters,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val pendingSyncDao: RunPendingSyncDao
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5) {
            return Result.failure()
        }

        val runId = workerParams.inputData.getString(RUN_ID) ?: return Result.failure()
        return when(val result = remoteRunDataSource.deleteRun(runId)) {
            is com.kiras.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }
            is com.kiras.core.domain.util.Result.Success -> {
                pendingSyncDao.deleteRunPendingSyncEntity(runId)
                Result.success()
            }
        }
    }

    companion object {
        const val RUN_ID = "RUN_ID"
    }
}
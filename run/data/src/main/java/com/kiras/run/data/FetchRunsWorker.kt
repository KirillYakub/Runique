package com.kiras.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kiras.core.domain.run.RunRepository
import com.kiras.core.domain.util.DataError

class FetchRunsWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val runRepository: RunRepository
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if(runAttemptCount >= 5) {
            return Result.failure()
        }
        return when(val result = runRepository.fetchRuns()) {
            is com.kiras.core.domain.util.Result.Error -> {
                result.error.toWorkerResult()
            }
            is com.kiras.core.domain.util.Result.Success<*> -> Result.success()
        }
    }
}
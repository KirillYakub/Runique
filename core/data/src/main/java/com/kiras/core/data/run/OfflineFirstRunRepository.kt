package com.kiras.core.data.run

import android.util.Log
import com.kiras.core.data.networking.get
import com.kiras.core.database.dao.RunPendingSyncDao
import com.kiras.core.database.mappers.toRun
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.run.LocalRunDataSource
import com.kiras.core.domain.run.RemoteRunDataSource
import com.kiras.core.domain.run.Run
import com.kiras.core.domain.run.RunId
import com.kiras.core.domain.run.RunRepository
import com.kiras.core.domain.run.SyncRunScheduler
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import com.kiras.core.domain.util.Result
import com.kiras.core.domain.util.asEmptyDataResult
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val syncRunScheduler: SyncRunScheduler,
    private val client: HttpClient
): RunRepository {

    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when(val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray
    ): EmptyResult<DataError> {
        val result = localRunDataSource.upsertRun(run)
        if(result !is Result.Success) {
            return result.asEmptyDataResult()
        }
        val runWithId = run.copy(id = result.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )
        return when(remoteResult) {
            is Result.Error -> {
                applicationScope.launch {
                    syncRunScheduler.scheduleSync(
                        type = SyncRunScheduler.SyncType.CreateRun(
                            run = runWithId,
                            mapPictureBytes = mapPicture
                        )
                    )
                }.join()
                Result.Success(Unit)
            }
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(id) != null
        if(isPendingSync) {
            runPendingSyncDao.deleteRunPendingSyncEntity(id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id)
        }.await()

        if(remoteResult is Result.Error) {
            applicationScope.launch {
                syncRunScheduler.scheduleSync(
                    type = SyncRunScheduler.SyncType.DeleteRun(id)
                )
            }.join()
        }
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId)
            }

            val createdJobs = createdRuns
                .await()
                .map { runPendingSyncEntity ->
                    launch {
                        val run = runPendingSyncEntity.run.toRun()
                        when(remoteRunDataSource.postRun(run, runPendingSyncEntity.mapPictureBytes)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(
                                        runPendingSyncEntity.runId
                                    )
                                }.join()
                            }
                        }
                    }
                }
            val deleteJobs = deletedRuns
                .await()
                .map { deletedRunSyncEntity ->
                    launch {
                        when(remoteRunDataSource.deleteRun(deletedRunSyncEntity.runId)) {
                            is Result.Error -> Unit
                            is Result.Success -> {
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(
                                        deletedRunSyncEntity.runId
                                    )
                                }.join()
                            }
                        }
                    }
                }

            createdJobs.forEach { it.join() }
            deleteJobs.forEach { it.join() }
        }
    }

    override suspend fun deleteAllRuns() {
        localRunDataSource.deleteAllRuns()
    }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val result = client.get<Unit>(route = "/logout").asEmptyDataResult()

        client.authProvider<BearerAuthProvider>()?.clearToken()

        return result
    }
}
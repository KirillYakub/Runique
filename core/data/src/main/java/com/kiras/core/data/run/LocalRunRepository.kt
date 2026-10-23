package com.kiras.core.data.run

import android.content.Context
import android.net.Uri
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.run.LocalRunDataSource
import com.kiras.core.domain.run.Run
import com.kiras.core.domain.run.RunId
import com.kiras.core.domain.run.RunRepository
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import com.kiras.core.domain.util.Result
import com.kiras.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import org.bson.types.ObjectId
import timber.log.Timber
import java.io.File
import java.io.IOException

class LocalRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope,
    private val context: Context
): RunRepository {

    private val mapPicturesDir: File
        get() = File(context.filesDir, MAP_PICTURES_DIR)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getRuns(): Flow<List<Run>> {
        return flow { emit(activeUserId()) }
            .flatMapLatest { userId ->
                if(userId == null) flowOf(emptyList())
                else localRunDataSource.getRuns(userId)
            }
    }

    override suspend fun upsertRun(
        run: Run,
        mapPicture: ByteArray
    ): EmptyResult<DataError.Local> {
        val userId = activeUserId() ?: return Result.Error(DataError.Local.NO_ACTIVE_SESSION)
        val runId = run.id ?: ObjectId().toHexString()

        return applicationScope.async {
            val mapPictureUrl = saveMapPicture(runId, mapPicture) ?: run.mapPictureUrl
            localRunDataSource.upsertRun(
                run = run.copy(id = runId, mapPictureUrl = mapPictureUrl),
                userId = userId
            ).asEmptyDataResult()
        }.await()
    }

    override suspend fun deleteRun(id: RunId) {
        val userId = activeUserId() ?: return
        applicationScope.async {
            localRunDataSource.deleteRun(id, userId)
            withContext(Dispatchers.IO) {
                mapPictureFile(id).delete()
            }
        }.await()
    }

    override suspend fun deleteAllRuns() {
        val userId = activeUserId() ?: return
        localRunDataSource.deleteAllRuns(userId)
    }

    private suspend fun activeUserId(): String? = sessionStorage.get()?.userId

    private suspend fun saveMapPicture(runId: RunId, bytes: ByteArray): String? {
        if(bytes.isEmpty()) return null
        return withContext(Dispatchers.IO) {
            try {
                mapPicturesDir.mkdirs()
                val file = mapPictureFile(runId)
                file.writeBytes(bytes)
                Uri.fromFile(file).toString()
            } catch (e: IOException) {
                Timber.e(e, "Could not save map picture for run $runId")
                null
            }
        }
    }

    private fun mapPictureFile(runId: RunId): File = File(mapPicturesDir, "$runId.jpg")

    private companion object {
        const val MAP_PICTURES_DIR = "run_maps"
    }
}

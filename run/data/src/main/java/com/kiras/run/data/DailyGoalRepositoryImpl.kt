package com.kiras.run.data

import android.database.sqlite.SQLiteFullException
import com.kiras.core.database.dao.DailyGoalDao
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import com.kiras.core.domain.util.Result
import com.kiras.run.data.mappers.toDailyGoal
import com.kiras.run.data.mappers.toDailyGoalEntity
import com.kiras.run.domain.goal.DailyGoal
import com.kiras.run.domain.goal.DailyGoalRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DailyGoalRepositoryImpl(
    private val dailyGoalDao: DailyGoalDao,
    private val sessionStorage: SessionStorage
): DailyGoalRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getDailyGoal(): Flow<DailyGoal?> {
        return flow { emit(activeUserId()) }
            .flatMapLatest { userId ->
                if(userId == null) flowOf(null)
                else dailyGoalDao.getDailyGoal(userId).map { it?.toDailyGoal() }
            }
    }

    override suspend fun saveDailyGoal(goal: DailyGoal): EmptyResult<DataError.Local> {
        val userId = activeUserId() ?: return Result.Error(DataError.Local.NO_ACTIVE_SESSION)
        return withContext(Dispatchers.IO) {
            try {
                dailyGoalDao.upsertDailyGoal(goal.toDailyGoalEntity(userId))
                Result.Success(Unit)
            } catch (e: SQLiteFullException) {
                Result.Error(DataError.Local.DISC_FULL)
            }
        }
    }

    private suspend fun activeUserId(): String? = sessionStorage.get()?.userId
}

package com.kiras.run.domain.goal

import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface DailyGoalRepository {
    fun getDailyGoal(): Flow<DailyGoal?>
    suspend fun saveDailyGoal(goal: DailyGoal): EmptyResult<DataError.Local>
}

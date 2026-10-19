package com.kiras.analytics.data

import com.kiras.analytics.domain.AnalyticsRepository
import com.kiras.analytics.domain.AnalyticsValues
import com.kiras.core.database.dao.AnalyticsDao
import com.kiras.core.domain.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class RoomAnalyticsRepository(
    private val analyticsDao: AnalyticsDao,
    private val sessionStorage: SessionStorage
): AnalyticsRepository {

    override suspend fun getAnalyticsValues(): AnalyticsValues {
        val userId = sessionStorage.get()?.userId ?: return AnalyticsValues()
        return withContext(Dispatchers.IO) {
            val totalDistance = async { analyticsDao.getTotalDistanceRun(userId) }
            val totalTimeMillis = async { analyticsDao.getTotalTimeRun(userId) }
            val maxRunSpeed = async { analyticsDao.getMaxRunSpeed(userId) }
            val avgDistancePerRun = async { analyticsDao.getAvgDistancePerRun(userId) }
            val avgPacePerRun = async { analyticsDao.getAvgPacePerRun(userId) }

            AnalyticsValues(
                totalDistanceRun = totalDistance.await() ?: 0,
                totalTimeRun = (totalTimeMillis.await() ?: 0L).toDuration(DurationUnit.MILLISECONDS),
                fastestEverRun = maxRunSpeed.await() ?: 0.0,
                avgDistancePerRun = avgDistancePerRun.await() ?: 0.0,
                avgPacePerRun = avgPacePerRun.await() ?: 0.0
            )
        }
    }
}

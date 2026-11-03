package com.kiras.statistics.data

import com.kiras.core.database.dao.StatisticsDao
import com.kiras.core.domain.SessionStorage
import com.kiras.statistics.domain.StatisticsRepository
import com.kiras.statistics.domain.StatisticsValues
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao,
    private val sessionStorage: SessionStorage
): StatisticsRepository {

    override suspend fun getStatisticsValues(): StatisticsValues {
        val userId = sessionStorage.get()?.userId ?: return StatisticsValues()
        return withContext(Dispatchers.IO) {
            val totalDistance = async { statisticsDao.getTotalDistanceRun(userId) }
            val totalTimeMillis = async { statisticsDao.getTotalTimeRun(userId) }
            val maxRunSpeed = async { statisticsDao.getMaxRunSpeed(userId) }
            val avgDistancePerRun = async { statisticsDao.getAvgDistancePerRun(userId) }
            val avgPacePerRun = async { statisticsDao.getAvgPacePerRun(userId) }

            StatisticsValues(
                totalDistanceRun = totalDistance.await() ?: 0,
                totalTimeRun = (totalTimeMillis.await() ?: 0L).toDuration(DurationUnit.MILLISECONDS),
                fastestEverRun = maxRunSpeed.await() ?: 0.0,
                avgDistancePerRun = avgDistancePerRun.await() ?: 0.0,
                avgPacePerRun = avgPacePerRun.await() ?: 0.0
            )
        }
    }
}

package com.kiras.statistics.data

import com.kiras.core.database.dao.StatisticsDao
import com.kiras.core.domain.SessionStorage
import com.kiras.statistics.data.mappers.toRunStatsEntry
import com.kiras.statistics.domain.RunStatsEntry
import com.kiras.statistics.domain.StatisticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class StatisticsRepositoryImpl(
    private val statisticsDao: StatisticsDao,
    private val sessionStorage: SessionStorage
): StatisticsRepository {

    override fun getRunEntries(): Flow<List<RunStatsEntry>> {
        return flow {
            val userId = sessionStorage.get()?.userId
            if(userId == null) {
                emit(emptyList())
            } else {
                emitAll(
                    statisticsDao.getRunStats(userId).map { rows ->
                        rows.map { it.toRunStatsEntry() }
                    }
                )
            }
        }.flowOn(Dispatchers.IO)
    }
}

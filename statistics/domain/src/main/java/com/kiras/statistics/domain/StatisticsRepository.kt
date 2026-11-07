package com.kiras.statistics.domain

import kotlinx.coroutines.flow.Flow

interface StatisticsRepository {
    fun getRunEntries(): Flow<List<RunStatsEntry>>
}

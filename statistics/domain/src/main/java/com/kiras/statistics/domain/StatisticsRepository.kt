package com.kiras.statistics.domain

interface StatisticsRepository {
    suspend fun getStatisticsValues(): StatisticsValues
}

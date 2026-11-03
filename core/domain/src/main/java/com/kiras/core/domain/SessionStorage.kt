package com.kiras.core.domain

import kotlinx.coroutines.flow.Flow

interface SessionStorage {
    suspend fun get(): AuthInfo?
    suspend fun set(info: AuthInfo?)
    fun observe(): Flow<AuthInfo?>
}

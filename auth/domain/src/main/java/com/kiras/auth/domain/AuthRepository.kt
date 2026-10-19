package com.kiras.auth.domain

import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult

interface AuthRepository {
    suspend fun login(email: String, password: String): EmptyResult<DataError.Local>
    suspend fun register(email: String, password: String): EmptyResult<DataError.Local>
}

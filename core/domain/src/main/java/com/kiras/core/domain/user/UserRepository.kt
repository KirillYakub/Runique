package com.kiras.core.domain.user

import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.Result

interface UserRepository {
    suspend fun getUser(userId: String): Result<User, DataError.Local>
}

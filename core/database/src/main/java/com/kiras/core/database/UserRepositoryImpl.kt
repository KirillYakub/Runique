package com.kiras.core.database

import com.kiras.core.database.dao.UserDao
import com.kiras.core.database.mappers.toUser
import com.kiras.core.domain.user.User
import com.kiras.core.domain.user.UserRepository
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUser(userId: String): Result<User, DataError.Local> {
        val user = withContext(Dispatchers.IO) {
            userDao.getUserById(userId)
        } ?: return Result.Error(DataError.Local.USER_NOT_FOUND)
        return Result.Success(user.toUser())
    }
}

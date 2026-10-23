package com.kiras.auth.data.fakes

import com.kiras.core.database.dao.UserDao
import com.kiras.core.database.entity.UserEntity

class FakeUserDao : UserDao {
    val users = mutableListOf<UserEntity>()

    override suspend fun insertUser(user: UserEntity) {
        users += user
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return users.firstOrNull { it.email == email }
    }

    override suspend fun getUserById(userId: String): UserEntity? {
        return users.firstOrNull { it.userId == userId }
    }
}

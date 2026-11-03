package com.kiras.core.database.mappers

import com.kiras.core.database.entity.UserEntity
import com.kiras.core.domain.user.User

fun UserEntity.toUser(): User {
    return User(
        userId = userId,
        email = email,
        username = username
    )
}
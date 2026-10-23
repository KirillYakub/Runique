package com.kiras.auth.data.mappers

import com.kiras.core.database.entity.UserEntity
import com.kiras.core.domain.AuthInfo

fun UserEntity.toAuthInfo(): AuthInfo {
    return AuthInfo(
        userId = userId
    )
}

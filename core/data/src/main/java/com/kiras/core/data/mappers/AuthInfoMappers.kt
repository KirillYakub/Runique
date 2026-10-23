package com.kiras.core.data.mappers

import com.kiras.core.data.auth.AuthInfoSerializable
import com.kiras.core.domain.AuthInfo

fun AuthInfo.toAuthInfoSerializable(): AuthInfoSerializable {
    return AuthInfoSerializable(
        userId = userId
    )
}

fun AuthInfoSerializable.toAuthInfo(): AuthInfo {
    return AuthInfo(
        userId = userId
    )
}
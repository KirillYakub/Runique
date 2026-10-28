package com.kiras.core.domain.util

sealed interface DataError: Error {
    enum class Network: DataError {
        REQUEST_TIMEOUT,
        UNAUTHORIZED,
        CONFLICT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        PAYLOAD_TOO_LARGE,
        SERVER_ERROR,
        SERIALIZATION,
        UNKNOWN
    }
    enum class Local: DataError {
        DISC_FULL,
        CONFLICT,
        INVALID_CREDENTIALS,
        NO_ACTIVE_SESSION,
        USER_NOT_FOUND
    }
}

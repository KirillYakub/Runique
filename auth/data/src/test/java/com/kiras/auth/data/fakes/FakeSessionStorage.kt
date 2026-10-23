package com.kiras.auth.data.fakes

import com.kiras.core.domain.AuthInfo
import com.kiras.core.domain.SessionStorage

class FakeSessionStorage : SessionStorage {
    var info: AuthInfo? = null

    override suspend fun get(): AuthInfo? = info

    override suspend fun set(info: AuthInfo?) {
        this.info = info
    }
}

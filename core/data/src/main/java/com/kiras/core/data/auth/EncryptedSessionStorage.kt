package com.kiras.core.data.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import com.kiras.core.data.mappers.toAuthInfo
import com.kiras.core.data.mappers.toAuthInfoSerializable
import com.kiras.core.domain.AuthInfo
import com.kiras.core.domain.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class EncryptedSessionStorage(
    private val sharedPreferences: SharedPreferences
): SessionStorage {

    private val json = Json { ignoreUnknownKeys = true }

    private val authInfo = MutableStateFlow<AuthInfo?>(null)
    private val authInfoMutex = Mutex()
    private var isAuthInfoLoaded = false

    override suspend fun get(): AuthInfo? {
        return withContext(Dispatchers.IO) {
            val stored = sharedPreferences.getString(KEY_AUTH_INFO, null)
            stored?.let { json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo() }
        }
    }

    override suspend fun set(info: AuthInfo?) {
        authInfoMutex.withLock {
            withContext(Dispatchers.IO) {
                if(info == null)
                    sharedPreferences.edit { remove(KEY_AUTH_INFO) }
                else {
                    val encoded = json.encodeToString(info.toAuthInfoSerializable())
                    sharedPreferences.edit { putString(KEY_AUTH_INFO, encoded) }
                }
            }
            authInfo.value = info
            isAuthInfoLoaded = true
        }
    }

    override fun observe(): Flow<AuthInfo?> {
        return flow {
            authInfoMutex.withLock {
                if(!isAuthInfoLoaded) {
                    authInfo.value = get()
                    isAuthInfoLoaded = true
                }
            }
            emitAll(authInfo)
        }
    }

    private companion object {
        const val KEY_AUTH_INFO = "auth_info"
    }
}

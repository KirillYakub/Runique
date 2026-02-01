package com.kiras.core.data.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import com.kiras.core.domain.AuthInfo
import com.kiras.core.domain.SessionStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class EncryptedSessionStorage(
    private val sharedPreferences: SharedPreferences
): SessionStorage {

    override suspend fun get(): AuthInfo? {
        return withContext(Dispatchers.IO) {
            val json = sharedPreferences.getString(KEY_AUTH_INFO, null)
            json?.let { Json.decodeFromString<AuthInfoSerializable>(it).toAuthInfo() }
        }
    }

    override suspend fun set(info: AuthInfo?) {
        withContext(Dispatchers.IO) {
            if(info == null)
                sharedPreferences.edit { remove(KEY_AUTH_INFO) }
            else {
                val json = Json.encodeToString(info.toAuthInfoSerializable())
                sharedPreferences.edit { putString(KEY_AUTH_INFO, json) }
            }
        }
    }

    companion object {
        private const val KEY_AUTH_INFO = "auth_info"
    }
}
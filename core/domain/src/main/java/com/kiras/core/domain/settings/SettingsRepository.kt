package com.kiras.core.domain.settings

import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getSettings(): Flow<UserSettings>
    suspend fun saveSettings(settings: UserSettings): EmptyResult<DataError.Local>
}

package com.kiras.core.database

import android.database.sqlite.SQLiteFullException
import com.kiras.core.database.dao.UserSettingsDao
import com.kiras.core.database.mappers.toUserSettings
import com.kiras.core.database.mappers.toUserSettingsEntity
import com.kiras.core.domain.SessionStorage
import com.kiras.core.domain.settings.SettingsRepository
import com.kiras.core.domain.settings.UserSettings
import com.kiras.core.domain.util.DataError
import com.kiras.core.domain.util.EmptyResult
import com.kiras.core.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val userSettingsDao: UserSettingsDao,
    private val sessionStorage: SessionStorage
) : SettingsRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSettings(): Flow<UserSettings> {
        return sessionStorage.observe()
            .map { it?.userId }
            .distinctUntilChanged()
            .flatMapLatest { userId ->
                if(userId == null) flowOf(UserSettings())
                else userSettingsDao.getSettings(userId).map { it?.toUserSettings() ?: UserSettings() }
            }
            .distinctUntilChanged()
    }

    override suspend fun saveSettings(settings: UserSettings): EmptyResult<DataError.Local> {
        val userId = sessionStorage.get()?.userId
            ?: return Result.Error(DataError.Local.NO_ACTIVE_SESSION)
        return withContext(Dispatchers.IO) {
            try {
                userSettingsDao.upsertSettings(settings.toUserSettingsEntity(userId))
                Result.Success(Unit)
            } catch (_: SQLiteFullException) {
                Result.Error(DataError.Local.DISC_FULL)
            }
        }
    }
}

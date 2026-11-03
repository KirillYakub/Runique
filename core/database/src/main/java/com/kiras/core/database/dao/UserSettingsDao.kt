package com.kiras.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kiras.core.database.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserSettingsDao {

    @Upsert
    suspend fun upsertSettings(settings: UserSettingsEntity)

    @Query("SELECT * FROM user_settings WHERE userId = :userId LIMIT 1")
    fun getSettings(userId: String): Flow<UserSettingsEntity?>
}

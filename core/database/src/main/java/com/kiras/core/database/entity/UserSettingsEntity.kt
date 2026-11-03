package com.kiras.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_settings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserSettingsEntity(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    val distanceUnit: String,
    val paceFormat: String,
    val countdownSeconds: Int,
    val keepScreenOn: Boolean,
    val gpsAccuracy: String,
    val theme: String
)

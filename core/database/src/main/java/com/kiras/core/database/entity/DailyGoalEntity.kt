package com.kiras.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_goals",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DailyGoalEntity(
    @PrimaryKey(autoGenerate = false)
    val userId: String,
    val metric: String,
    val target: Long,
    val goalDays: Int
)

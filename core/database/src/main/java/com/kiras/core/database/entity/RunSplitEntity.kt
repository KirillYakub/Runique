package com.kiras.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "runsplitentity",
    primaryKeys = ["runId", "distanceUnit", "number"],
    foreignKeys = [
        ForeignKey(
            entity = RunEntity::class,
            parentColumns = ["id"],
            childColumns = ["runId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("runId")]
)
data class RunSplitEntity(
    val runId: String,
    val distanceUnit: String,
    val number: Int,
    val distanceMeters: Double,
    val durationMillis: Long
)

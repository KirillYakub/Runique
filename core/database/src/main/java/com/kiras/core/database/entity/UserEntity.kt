package com.kiras.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import org.bson.types.ObjectId

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val userId: String = ObjectId().toHexString(),
    val email: String,
    val passwordHash: String
)

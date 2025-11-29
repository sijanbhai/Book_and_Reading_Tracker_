package com.sijan.bookandreadingtracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String, // In production, this should be hashed
    val createdAt: Long = System.currentTimeMillis()
)


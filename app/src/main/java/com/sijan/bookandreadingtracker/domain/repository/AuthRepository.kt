package com.sijan.bookandreadingtracker.domain.repository

import com.sijan.bookandreadingtracker.data.local.UserEntity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<Long>
    suspend fun login(email: String, password: String): Result<UserEntity>
    suspend fun getCurrentUser(): UserEntity?
    fun observeCurrentUser(): Flow<UserEntity?>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}


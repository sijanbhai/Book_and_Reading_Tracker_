package com.sijan.bookandreadingtracker.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.sijan.bookandreadingtracker.data.local.UserDao
import com.sijan.bookandreadingtracker.data.local.UserEntity
import com.sijan.bookandreadingtracker.domain.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    @ApplicationContext private val context: Context
) : AuthRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "auth_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USER_ID = "user_id"
    }

    override suspend fun register(name: String, email: String, password: String): Result<Long> {
        return try {
            // Check if email already exists
            if (userDao.isEmailExists(email) > 0) {
                return Result.failure(Exception("Email already registered"))
            }

            // Validate inputs
            if (name.isBlank()) {
                return Result.failure(Exception("Name cannot be empty"))
            }
            if (!isValidEmail(email)) {
                return Result.failure(Exception("Invalid email format"))
            }
            if (password.length < 6) {
                return Result.failure(Exception("Password must be at least 6 characters"))
            }

            val user = UserEntity(
                name = name,
                email = email,
                password = password // In production, hash this
            )
            val userId = userDao.insertUser(user)

            // Save user ID to shared preferences
            prefs.edit().putLong(KEY_USER_ID, userId).apply()

            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<UserEntity> {
        return try {
            val user = userDao.login(email, password)
            if (user != null) {
                // Save user ID to shared preferences
                prefs.edit().putLong(KEY_USER_ID, user.id).apply()
                Result.success(user)
            } else {
                Result.failure(Exception("Invalid email or password"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCurrentUser(): UserEntity? {
        val userId = prefs.getLong(KEY_USER_ID, -1)
        return if (userId != -1L) {
            userDao.getUserById(userId)
        } else {
            null
        }
    }

    override fun observeCurrentUser(): Flow<UserEntity?> {
        val userId = prefs.getLong(KEY_USER_ID, -1)
        return if (userId != -1L) {
            userDao.observeUserById(userId)
        } else {
            flow { emit(null) }
        }
    }

    override suspend fun logout() {
        prefs.edit().remove(KEY_USER_ID).apply()
    }

    override suspend fun isLoggedIn(): Boolean {
        val userId = prefs.getLong(KEY_USER_ID, -1)
        return userId != -1L && userDao.getUserById(userId) != null
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}


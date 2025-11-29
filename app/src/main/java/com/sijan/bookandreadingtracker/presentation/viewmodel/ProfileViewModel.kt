package com.sijan.bookandreadingtracker.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sijan.bookandreadingtracker.data.local.UserEntity
import com.sijan.bookandreadingtracker.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileState(
    val isLoggedIn: Boolean = false,
    val currentUser: UserEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)

sealed class AuthMode {
    object Login : AuthMode()
    object Register : AuthMode()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _authMode = MutableStateFlow<AuthMode>(AuthMode.Login)
    val authMode: StateFlow<AuthMode> = _authMode.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            if (isLoggedIn) {
                val user = authRepository.getCurrentUser()
                _state.value = _state.value.copy(
                    isLoggedIn = true,
                    currentUser = user
                )
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = authRepository.login(email, password)
            result.fold(
                onSuccess = { user ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        successMessage = "Welcome back, ${user.name}!"
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Login failed"
                    )
                }
            )
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)

            val result = authRepository.register(name, email, password)
            result.fold(
                onSuccess = { userId ->
                    // After successful registration, get the user
                    val user = authRepository.getCurrentUser()
                    _state.value = _state.value.copy(
                        isLoading = false,
                        isLoggedIn = true,
                        currentUser = user,
                        successMessage = "Account created successfully!"
                    )
                },
                onFailure = { exception ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Registration failed"
                    )
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _state.value = ProfileState()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearSuccessMessage() {
        _state.value = _state.value.copy(successMessage = null)
    }

    fun switchAuthMode() {
        _authMode.value = when (_authMode.value) {
            is AuthMode.Login -> AuthMode.Register
            is AuthMode.Register -> AuthMode.Login
        }
        clearError()
        clearSuccessMessage()
    }
}


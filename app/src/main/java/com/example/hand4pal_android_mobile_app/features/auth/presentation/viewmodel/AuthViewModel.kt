package com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.auth.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _loginResponse = MutableStateFlow<AuthResponse?>(null)
    val loginResponse: StateFlow<AuthResponse?> = _loginResponse

    fun registerCitizen(request: RegisterCitizenRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.registerCitizen(request)
                .onSuccess {
                    _authState.value = AuthState.Success("Registration successful! Please login.")
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Registration failed")
                }
        }
    }

    fun registerAssociation(request: RegisterAssociationRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.registerAssociation(request)
                .onSuccess {
                    _authState.value = AuthState.Success("Association registration submitted! Awaiting approval. You can login once approved.")
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Registration failed")
                }
        }
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            repository.login(request)
                .onSuccess { response ->
                    _loginResponse.value = response
                    _authState.value = AuthState.Success("Login successful")
                }
                .onFailure { error ->
                    _authState.value = AuthState.Error(error.message ?: "Login failed")
                }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

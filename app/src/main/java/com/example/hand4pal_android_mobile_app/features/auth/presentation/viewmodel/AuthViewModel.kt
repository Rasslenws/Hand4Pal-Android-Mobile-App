package com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.auth.domain.* // Imports User, RegisterUserRequest
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // State for Login
    private val _loginState = MutableLiveData<Result<AuthResponse>>()
    val loginState: LiveData<Result<AuthResponse>> = _loginState

    // State for Register (Returns User object, NOT token)
    private val _registerState = MutableLiveData<Result<User>>()
    val registerState: LiveData<Result<User>> = _registerState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            val request = AuthRequest(email, pass)
            repository.login(request).collect { result ->
                _loginState.value = result
            }
        }
    }

    fun register(firstName: String, lastName: String, email: String, pass: String) {
        viewModelScope.launch {
            // Create the request object matching backend DTO
            val request = RegisterUserRequest(firstName, lastName, email, pass)

            // Call Repository
            repository.register(request).collect { result ->
                _registerState.value = result
            }
        }
    }
}

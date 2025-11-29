package com.example.hand4pal_android_mobile_app.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.profile.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {
    
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState
    
    private val _updateState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val updateState: StateFlow<ProfileState> = _updateState
    
    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            repository.getProfile()
                .onSuccess { profile ->
                    _profileState.value = ProfileState.Success(profile)
                }
                .onFailure { error ->
                    _profileState.value = ProfileState.Error(error.message ?: "Failed to load profile")
                }
        }
    }
    
    fun updateProfile(request: UpdateProfileRequest) {
        viewModelScope.launch {
            _updateState.value = ProfileState.Loading
            repository.updateProfile(request)
                .onSuccess { profile ->
                    _updateState.value = ProfileState.Success(profile)
                    _profileState.value = ProfileState.Success(profile)
                }
                .onFailure { error ->
                    _updateState.value = ProfileState.Error(error.message ?: "Failed to update profile")
                }
        }
    }
    
    fun changePassword(request: ChangePasswordRequest) {
        viewModelScope.launch {
            _updateState.value = ProfileState.Loading
            repository.changePassword(request)
                .onSuccess { message ->
                    _updateState.value = ProfileState.Error(message) // Using Error to show message
                }
                .onFailure { error ->
                    _updateState.value = ProfileState.Error(error.message ?: "Failed to change password")
                }
        }
    }
    
    fun resetUpdateState() {
        _updateState.value = ProfileState.Idle
    }
}

class ProfileViewModelFactory(
    private val repository: ProfileRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

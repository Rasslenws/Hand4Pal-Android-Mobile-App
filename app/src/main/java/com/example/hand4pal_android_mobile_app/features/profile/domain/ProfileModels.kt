package com.example.hand4pal_android_mobile_app.features.profile.domain

// Response Model
data class ProfileResponse(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val profilePictureUrl: String?,
    val role: String,
    val inscriptionDate: String,
    val hasPassword: Boolean,
    val isGoogleLinked: Boolean
)

// Request Models
data class UpdateProfileRequest(
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val phone: String?,
    val profilePictureUrl: String?
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

// UI State
sealed class ProfileState {
    object Idle : ProfileState()
    object Loading : ProfileState()
    data class Success(val profile: ProfileResponse) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

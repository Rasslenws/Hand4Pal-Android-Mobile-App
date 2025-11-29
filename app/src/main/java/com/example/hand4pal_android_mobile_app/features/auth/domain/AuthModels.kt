package com.example.hand4pal_android_mobile_app.features.auth.domain

// Request Models
data class RegisterCitizenRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val password: String
)

data class RegisterAssociationRequest(
    val associationName: String,
    val ownerFirstName: String,
    val ownerLastName: String,
    val email: String,
    val phone: String,
    val password: String,
    val description: String?,
    val address: String?,
    val webSite: String?
)

data class LoginRequest(
    val email: String,
    val password: String
)

// Response Models
data class AuthResponse(
    val token: String,
    val userId: Long,
    val email: String,
    val role: String
)

data class UserResponse(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val inscriptionDate: String,
    val profilePictureUrl: String?
)

data class AssociationRequestResponse(
    val id: Long,
    val associationName: String,
    val ownerFirstName: String,
    val ownerLastName: String,
    val email: String,
    val phone: String,
    val status: String,
    val submittedAt: String
)

// UI State
sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

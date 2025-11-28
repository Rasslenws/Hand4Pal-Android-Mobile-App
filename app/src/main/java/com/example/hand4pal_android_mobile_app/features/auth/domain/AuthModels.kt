package com.example.hand4pal_android_mobile_app.features.auth.domain

// Matches Backend DTOs
data class AuthRequest(val email: String, val password: String)

data class AuthResponse(val token: String) // Backend only sends token in AuthResponse? Check DTO.

data class RegisterUserRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)

data class GoogleTokenRequest(val idToken: String)

// Matches the "User" entity returned by /register/citizen
data class User(
    val userId: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String
)

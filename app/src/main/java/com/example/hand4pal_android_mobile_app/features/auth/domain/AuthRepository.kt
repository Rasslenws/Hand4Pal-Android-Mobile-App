package com.example.hand4pal_android_mobile_app.features.auth.domain

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(request: AuthRequest): Flow<Result<AuthResponse>>
    fun register(request: RegisterUserRequest): Flow<Result<User>> // Returns User now
    fun googleAuth(idToken: String): Flow<Result<AuthResponse>>
}

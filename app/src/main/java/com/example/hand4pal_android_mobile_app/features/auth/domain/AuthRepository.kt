package com.example.hand4pal_android_mobile_app.features.auth.domain

interface AuthRepository {
    suspend fun registerCitizen(request: RegisterCitizenRequest): Result<UserResponse>
    suspend fun registerAssociation(request: RegisterAssociationRequest): Result<AssociationRequestResponse>
    suspend fun login(request: LoginRequest): Result<AuthResponse>
}

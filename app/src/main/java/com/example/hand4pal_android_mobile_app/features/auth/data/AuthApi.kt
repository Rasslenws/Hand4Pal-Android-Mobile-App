package com.example.hand4pal_android_mobile_app.features.auth.data

import com.example.hand4pal_android_mobile_app.features.auth.domain.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    // Endpoint: /auth/login
    @POST("auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    // Endpoint: /auth/register/citizen
    // Returns User, NOT token
    @POST("auth/register/citizen")
    suspend fun registerCitizen(@Body request: RegisterUserRequest): Response<User>

    // Endpoint: /auth/google
    @POST("auth/google")
    suspend fun googleAuth(@Body request: GoogleTokenRequest): Response<AuthResponse>
}

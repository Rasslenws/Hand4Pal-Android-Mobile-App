package com.example.hand4pal_android_mobile_app.features.auth.data

import com.example.hand4pal_android_mobile_app.features.auth.domain.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    
    @POST("auth/register/citizen")
    suspend fun registerCitizen(
        @Body request: RegisterCitizenRequest
    ): Response<UserResponse>
    
    @POST("auth/register/association")
    suspend fun registerAssociation(
        @Body request: RegisterAssociationRequest
    ): Response<AssociationRequestResponse>
    
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>
}

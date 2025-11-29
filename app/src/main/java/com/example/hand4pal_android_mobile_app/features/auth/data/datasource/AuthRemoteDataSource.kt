package com.example.hand4pal_android_mobile_app.features.auth.data.datasource

import com.example.hand4pal_android_mobile_app.features.auth.data.AuthApi
import com.example.hand4pal_android_mobile_app.features.auth.domain.*
import retrofit2.Response

class AuthRemoteDataSource(private val authApi: AuthApi) {
    
    suspend fun registerCitizen(request: RegisterCitizenRequest): Response<UserResponse> {
        return authApi.registerCitizen(request)
    }
    
    suspend fun registerAssociation(request: RegisterAssociationRequest): Response<AssociationRequestResponse> {
        return authApi.registerAssociation(request)
    }
    
    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return authApi.login(request)
    }
}

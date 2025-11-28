package com.example.hand4pal_android_mobile_app.features.auth.data.datasource

import com.example.hand4pal_android_mobile_app.features.auth.data.AuthApi
import com.example.hand4pal_android_mobile_app.features.auth.domain.AuthRequest
import com.example.hand4pal_android_mobile_app.features.auth.domain.RegisterUserRequest

class AuthRemoteDataSource(private val api: AuthApi) {
    suspend fun login(request: AuthRequest) = api.login(request)
    suspend fun register(request: RegisterUserRequest) = api.registerCitizen(request)
}

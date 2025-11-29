package com.example.hand4pal_android_mobile_app.features.profile.data

import com.example.hand4pal_android_mobile_app.features.profile.domain.*
import retrofit2.Response
import retrofit2.http.*

interface ProfileApi {
    
    @GET("profile")
    suspend fun getProfile(): Response<ProfileResponse>
    
    @PUT("profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>
    
    @POST("profile/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<Map<String, String>>
    
    @DELETE("profile")
    suspend fun deleteAccount(
        @Body password: Map<String, String>?
    ): Response<Map<String, String>>
}

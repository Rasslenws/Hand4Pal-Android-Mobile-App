package com.example.hand4pal_android_mobile_app.features.profile.domain

import com.example.hand4pal_android_mobile_app.features.profile.domain.*

interface ProfileRepository {
    suspend fun getProfile(): Result<ProfileResponse>
    suspend fun updateProfile(request: UpdateProfileRequest): Result<ProfileResponse>
    suspend fun changePassword(request: ChangePasswordRequest): Result<String>
    suspend fun deleteAccount(password: String?): Result<String>
}

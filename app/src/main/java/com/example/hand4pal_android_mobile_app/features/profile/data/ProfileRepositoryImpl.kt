package com.example.hand4pal_android_mobile_app.features.profile.data

import com.example.hand4pal_android_mobile_app.features.profile.domain.*
import com.google.gson.Gson

class ProfileRepositoryImpl(
    private val api: ProfileApi
) : ProfileRepository {
    
    override suspend fun getProfile(): Result<ProfileResponse> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to load profile"
                } catch (e: Exception) {
                    "Failed to load profile"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateProfile(request: UpdateProfileRequest): Result<ProfileResponse> {
        return try {
            val response = api.updateProfile(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to update profile"
                } catch (e: Exception) {
                    "Failed to update profile"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun changePassword(request: ChangePasswordRequest): Result<String> {
        return try {
            val response = api.changePassword(request)
            if (response.isSuccessful) {
                val message = response.body()?.get("message") ?: "Password changed successfully"
                Result.success(message)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to change password"
                } catch (e: Exception) {
                    "Failed to change password"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun deleteAccount(password: String?): Result<String> {
        return try {
            val body = if (password != null) mapOf("password" to password) else null
            val response = api.deleteAccount(body)
            if (response.isSuccessful) {
                val message = response.body()?.get("message") ?: "Account deleted successfully"
                Result.success(message)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to delete account"
                } catch (e: Exception) {
                    "Failed to delete account"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

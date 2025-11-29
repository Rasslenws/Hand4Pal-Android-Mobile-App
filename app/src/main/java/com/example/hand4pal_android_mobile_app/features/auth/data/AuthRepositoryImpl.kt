package com.example.hand4pal_android_mobile_app.features.auth.data

import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthLocalDataSource
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthRemoteDataSource
import com.example.hand4pal_android_mobile_app.features.auth.domain.*
import com.google.gson.Gson

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    override suspend fun registerCitizen(request: RegisterCitizenRequest): Result<UserResponse> {
        return try {
            val response = remoteDataSource.registerCitizen(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Registration failed"
                } catch (e: Exception) {
                    "Registration failed"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun registerAssociation(request: RegisterAssociationRequest): Result<AssociationRequestResponse> {
        return try {
            val response = remoteDataSource.registerAssociation(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Registration failed"
                } catch (e: Exception) {
                    "Registration failed"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(request: LoginRequest): Result<AuthResponse> {
        return try {
            val response = remoteDataSource.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Save token locally
                localDataSource.saveToken(authResponse.token)
                Result.success(authResponse)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Login failed"
                } catch (e: Exception) {
                    "Login failed. Please check your credentials."
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}

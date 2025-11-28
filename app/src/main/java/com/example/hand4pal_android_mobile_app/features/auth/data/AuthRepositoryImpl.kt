package com.example.hand4pal_android_mobile_app.features.auth.data.repository

import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthLocalDataSource
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthRemoteDataSource
import com.example.hand4pal_android_mobile_app.features.auth.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl(
    private val remoteDataSource: AuthRemoteDataSource,
    private val localDataSource: AuthLocalDataSource
) : AuthRepository {

    override fun login(request: AuthRequest): Flow<Result<AuthResponse>> = flow {
        try {
            val response = remoteDataSource.login(request)
            if (response.isSuccessful && response.body() != null) {
                val authData = response.body()!!

                // Utilisation de la DataSource locale (DataStore/SharedPref)
                // Note: saveToken est suspendu si vous utilisez DataStore
                localDataSource.saveToken(authData.token)

                emit(Result.success(authData))
            } else {
                emit(Result.failure(Exception("Login failed: ${response.code()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun register(request: RegisterUserRequest): Flow<Result<User>> = flow {
        try {
            val response = remoteDataSource.register(request)
            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!
                // Pas de token retourné à l'inscription, donc rien à sauvegarder ici
                emit(Result.success(user))
            } else {
                emit(Result.failure(Exception("Registration failed: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun googleAuth(idToken: String): Flow<Result<AuthResponse>> = flow {
        try {
            // Assurez-vous d'ajouter googleAuth dans AuthRemoteDataSource aussi !
            // val response = remoteDataSource.googleAuth(idToken)
            // Pour l'instant, si ce n'est pas dans la DataSource, vous pouvez garder l'appel API direct si nécessaire,
            // mais l'idéal est de tout passer par RemoteDataSource.

            // Exemple hypothétique si ajouté dans RemoteDataSource :
            /*
            val response = remoteDataSource.googleAuth(idToken)
            if (response.isSuccessful && response.body() != null) {
                val authData = response.body()!!
                localDataSource.saveToken(authData.token)
                emit(Result.success(authData))
            } else {
                emit(Result.failure(Exception("Google Auth failed")))
            }
            */
            emit(Result.failure(Exception("Google Auth not implemented in DataSource yet")))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
}

package com.example.hand4pal_android_mobile_app.features.auth.data.datasource

import android.content.Context
import com.example.hand4pal_android_mobile_app.core.network.DataStoreKeys
import com.example.hand4pal_android_mobile_app.core.network.dataStore
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class AuthLocalDataSource private constructor(private val context: Context) {

    companion object {
        @Volatile
        private var INSTANCE: AuthLocalDataSource? = null

        fun getInstance(context: Context): AuthLocalDataSource {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthLocalDataSource(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }

    // Save token asynchronously
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.TOKEN_KEY] = token
        }
    }

    // Flow for observing token changes
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[DataStoreKeys.TOKEN_KEY] }

    // Direct read for Interceptor
    suspend fun getToken(): String? {
        return context.dataStore.data.map { it[DataStoreKeys.TOKEN_KEY] }.first()
    }

    // Clear token
    suspend fun clearToken() {
        context.dataStore.edit { it.remove(DataStoreKeys.TOKEN_KEY) }
    }
}

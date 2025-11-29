package com.example.hand4pal_android_mobile_app.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// 1. Extension Property (MUST be unique in the whole app)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

// 2. Keys Object
object DataStoreKeys {
    val TOKEN_KEY = stringPreferencesKey("jwt_token")
}

// 3. The Manager Class
class DataStoreManager(private val context: Context) {

    // Get Token
    suspend fun getToken(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.TOKEN_KEY]
        } catch (e: Exception) {
            null
        }
    }

    // Save Token
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.TOKEN_KEY] = token
        }
    }

    // Clear Token
    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(DataStoreKeys.TOKEN_KEY)
        }
    }

    // Token Flow (for observing)
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[DataStoreKeys.TOKEN_KEY] }
}

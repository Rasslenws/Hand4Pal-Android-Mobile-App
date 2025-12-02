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
    val USER_ROLE_KEY = stringPreferencesKey("user_role")
    val USER_ID_KEY = stringPreferencesKey("user_id")
    val USER_EMAIL_KEY = stringPreferencesKey("user_email")
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

    // Save User Role
    suspend fun saveUserRole(role: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.USER_ROLE_KEY] = role
        }
    }

    // Get User Role
    suspend fun getUserRole(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.USER_ROLE_KEY]
        } catch (e: Exception) {
            null
        }
    }

    // User Role Flow (for observing)
    val userRoleFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[DataStoreKeys.USER_ROLE_KEY] }

    // Save User ID
    suspend fun saveUserId(userId: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.USER_ID_KEY] = userId
        }
    }

    // Get User ID
    suspend fun getUserId(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.USER_ID_KEY]
        } catch (e: Exception) {
            null
        }
    }

    // Save User Email
    suspend fun saveUserEmail(email: String) {
        context.dataStore.edit { preferences ->
            preferences[DataStoreKeys.USER_EMAIL_KEY] = email
        }
    }

    // Get User Email
    suspend fun getUserEmail(): String? {
        return try {
            val preferences = context.dataStore.data.first()
            preferences[DataStoreKeys.USER_EMAIL_KEY]
        } catch (e: Exception) {
            null
        }
    }

    // Clear all user data
    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

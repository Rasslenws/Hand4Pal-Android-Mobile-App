package com.example.hand4pal_android_mobile_app.features.auth.data.datasource

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

// Extension pour créer le DataStore une seule fois
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

class AuthLocalDataSource(private val context: Context) {

    companion object {
        private val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
    }

    // Sauvegarde asynchrone
    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    // Lecture sous forme de Flow (Observateur)
    val tokenFlow: Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[JWT_TOKEN_KEY] }

    // Lecture directe (bloquante) pour l'Interceptor
    suspend fun getToken(): String? {
        return context.dataStore.data.map { it[JWT_TOKEN_KEY] }.first()
    }

    suspend fun clearToken() {
        context.dataStore.edit { it.remove(JWT_TOKEN_KEY) }
    }
}

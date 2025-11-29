package com.example.hand4pal_android_mobile_app.core.network

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

// Single DataStore instance for the entire app
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

object DataStoreKeys {
    val TOKEN_KEY = stringPreferencesKey("jwt_token")
}

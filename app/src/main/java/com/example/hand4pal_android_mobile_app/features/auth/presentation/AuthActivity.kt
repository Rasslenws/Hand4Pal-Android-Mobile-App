package com.example.hand4pal_android_mobile_app.features.auth.presentation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.example.hand4pal_android_mobile_app.MainActivity
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.AuthInterceptor
import com.example.hand4pal_android_mobile_app.core.network.DataStoreKeys
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.core.network.dataStore
import com.example.hand4pal_android_mobile_app.features.auth.data.AuthRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthLocalDataSource
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthRemoteDataSource
import com.example.hand4pal_android_mobile_app.features.auth.presentation.login.LoginFragment
import com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AuthActivity : AppCompatActivity() {

    val authViewModelFactory: AuthViewModelFactory by lazy {
        // Use singleton instance
        val localDataSource = AuthLocalDataSource.getInstance(this)
        val remoteDataSource = AuthRemoteDataSource(RetrofitClient.authApi)
        val repository = AuthRepositoryImpl(remoteDataSource, localDataSource)
        AuthViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Initialize AuthInterceptor with context
        RetrofitClient.init(applicationContext)
        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        // Check if already logged in
        lifecycleScope.launch {
            val token = dataStore.data.first()[DataStoreKeys.TOKEN_KEY]
            if (token != null) {
                // Already logged in, go to main
                navigateToMain()
                return@launch
            }
        }

        // Show login fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, LoginFragment())
                .commit()
        }
    }

    fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.TOKEN_KEY] = token
        }
    }

    suspend fun saveUserRole(role: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.USER_ROLE_KEY] = role
        }
    }

    suspend fun saveUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.USER_ID_KEY] = userId
        }
    }

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[DataStoreKeys.USER_EMAIL_KEY] = email
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

// core/network/AuthInterceptor.kt
package com.example.hand4pal_android_mobile_app.core.network

import android.content.Context
import android.content.Intent
import com.example.hand4pal_android_mobile_app.features.auth.presentation.AuthActivity
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.atomic.AtomicBoolean

class AuthInterceptor(
    private val context: Context, // Needed to start Activity
    private val dataStoreManager: DataStoreManager
) : Interceptor {

    companion object {
        // Prevents opening multiple Login screens if multiple requests fail at once
        private val isLoggingOut = AtomicBoolean(false)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // 1. Skip auth endpoints
        if (originalRequest.url.encodedPath.contains("/auth/")) {
            return chain.proceed(originalRequest)
        }

        // 2. Add Token Header
        val token = runBlocking { dataStoreManager.getToken() }
        android.util.Log.d("AuthInterceptor", "Token retrieved: $token")

        val newRequest = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            android.util.Log.w("AuthInterceptor", "No token available for ${originalRequest.url}")
            originalRequest
        }

        val response = chain.proceed(newRequest)

        // 3. Handle 401 (Token Expired)
        if (response.code == 401 || response.code == 403) {
            // Only one thread enters this block
            if (isLoggingOut.compareAndSet(false, true)) {
                runBlocking {
                    dataStoreManager.clearToken()
                }

                // Redirect to Login
                val intent = Intent(context, AuthActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)

                // Note: We don't reset isLoggingOut to false immediately
                // because the app is restarting anyway.
            }
        }

        return response
    }
}

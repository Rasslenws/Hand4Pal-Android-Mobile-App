package com.example.hand4pal_android_mobile_app.core.network

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor : Interceptor {
    
    companion object {
        private var context: Context? = null
        
        fun init(appContext: Context) {
            context = appContext.applicationContext
        }
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip adding token for auth endpoints
        if (originalRequest.url.encodedPath.contains("/auth/")) {
            return chain.proceed(originalRequest)
        }
        
        // Get token from DataStore using centralized key
        val token = context?.let { ctx ->
            runBlocking {
                try {
                    ctx.dataStore.data.first()[DataStoreKeys.TOKEN_KEY]
                } catch (e: Exception) {
                    null
                }
            }
        }
        
        // Add Authorization header if token exists
        val newRequest = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}

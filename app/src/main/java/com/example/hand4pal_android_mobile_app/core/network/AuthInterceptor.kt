package com.example.hand4pal_android_mobile_app.core.network

import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthLocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val localDataSource: AuthLocalDataSource) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Récupère le token depuis la DataSource
        // runBlocking est utilisé ici car intercept() est synchrone mais DataStore est asynchrone.
        // Si AuthLocalDataSource utilise SharedPreferences (synchrone), runBlocking est très rapide.
        val token = runBlocking {
            localDataSource.getToken()
        }

        // Si un token existe, on l'ajoute dans le Header Authorization
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}

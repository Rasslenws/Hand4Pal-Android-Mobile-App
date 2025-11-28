package com.example.hand4pal_android_mobile_app.core.network

import android.content.Context
import com.example.hand4pal_android_mobile_app.features.auth.data.AuthApi // Notez le changement de package si vous avez déplacé AuthApi
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthLocalDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Use 10.0.2.2 for Android Emulator to access localhost
    private const val BASE_URL = "http://10.0.2.2:8081/"

    private fun provideOkHttpClient(localDataSource: AuthLocalDataSource): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(localDataSource)) // Utilise maintenant LocalDataSource
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Crée l'instance de l'API.
     * Nécessite AuthLocalDataSource pour configurer l'intercepteur.
     */
    fun getAuthApi(context: Context, localDataSource: AuthLocalDataSource): AuthApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(provideOkHttpClient(localDataSource))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }
}

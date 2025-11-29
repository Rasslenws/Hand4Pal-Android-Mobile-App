package com.example.hand4pal_android_mobile_app.core.network

import android.content.Context
import com.example.hand4pal_android_mobile_app.features.auth.data.AuthApi
import com.example.hand4pal_android_mobile_app.features.profile.data.ProfileApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8081/"

    // APIs are initialized lazily
    lateinit var authApi: AuthApi
        private set
    lateinit var profileApi: ProfileApi
        private set

    private var isInitialized = false

    // Call this from your Application class or MainActivity onCreate
    fun init(context: Context) {
        if (isInitialized) return

        val dataStoreManager = DataStoreManager(context)

        // Pass Context and DataStoreManager to the Interceptor
        val authInterceptor = AuthInterceptor(context, dataStoreManager)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create API instances
        authApi = retrofit.create(AuthApi::class.java)
        profileApi = retrofit.create(ProfileApi::class.java)

        isInitialized = true
    }
}

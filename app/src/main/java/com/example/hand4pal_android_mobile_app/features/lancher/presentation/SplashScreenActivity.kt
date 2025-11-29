package com.example.hand4pal_android_mobile_app.features.lancher.presentation

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.hand4pal_android_mobile_app.MainActivity
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.AuthInterceptor
import com.example.hand4pal_android_mobile_app.core.network.DataStoreKeys
import com.example.hand4pal_android_mobile_app.core.network.dataStore
import com.example.hand4pal_android_mobile_app.features.auth.presentation.AuthActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        // Initialize AuthInterceptor with context
        AuthInterceptor.init(this)

        // Check if user is logged in after 2 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            lifecycleScope.launch {
                val token = dataStore.data.first()[DataStoreKeys.TOKEN_KEY]
                
                val intent = if (token != null) {
                    // User is logged in, go to main
                    Intent(this@SplashScreenActivity, MainActivity::class.java)
                } else {
                    // User not logged in, go to auth
                    Intent(this@SplashScreenActivity, AuthActivity::class.java)
                }
                
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }, 2000)
    }
}

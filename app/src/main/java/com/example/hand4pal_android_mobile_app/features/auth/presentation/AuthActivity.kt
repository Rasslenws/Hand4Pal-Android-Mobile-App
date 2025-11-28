package com.example.hand4pal_android_mobile_app.features.auth.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.auth.presentation.login.LoginFragment

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        // Charge le LoginFragment au démarrage
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }
    }
}

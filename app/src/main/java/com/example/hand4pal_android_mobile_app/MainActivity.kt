package com.example.hand4pal_android_mobile_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.hand4pal_android_mobile_app.core.network.DataStoreManager
import com.example.hand4pal_android_mobile_app.features.dashboard.presentation.AssociationDashboardFragment
import com.example.hand4pal_android_mobile_app.features.home.CampaignsFragment
import com.example.hand4pal_android_mobile_app.features.home.DonationFragment
import com.example.hand4pal_android_mobile_app.features.home.HomeFragment
import com.example.hand4pal_android_mobile_app.features.profile.presentation.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dataStoreManager = DataStoreManager(applicationContext)

        // Enable back button in action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        bottomNav = findViewById(R.id.bottom_navigation)

        // Configure bottom navigation based on user role
        lifecycleScope.launch {
            setupBottomNavigation()
        }

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private suspend fun setupBottomNavigation() {
        val userRole = dataStoreManager.getUserRole()

        // Add dashboard menu item for associations
        if (userRole == "ASSOCIATION") {
            bottomNav.menu.add(0, R.id.nav_dashboard, 0, "Dashboard")
                .setIcon(R.drawable.ic_dashboard)
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_donation -> DonationFragment()
                R.id.nav_campaign -> CampaignsFragment()
                R.id.nav_dashboard -> AssociationDashboardFragment()
                R.id.nav_profile -> ProfileFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

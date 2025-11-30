package com.example.hand4pal_android_mobile_app.features.donation.data

import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationHistory

class DonationRepositoryImpl {
    private val api = RetrofitClient.donationApi

    suspend fun getMyDonations(): Result<List<DonationHistory>> {
        return try {
            val response = api.getMyDonations()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

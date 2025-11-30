package com.example.hand4pal_android_mobile_app.features.donation.domain

interface DonationRepository {
    suspend fun getMyDonations(): Result<List<DonationHistory>>
}

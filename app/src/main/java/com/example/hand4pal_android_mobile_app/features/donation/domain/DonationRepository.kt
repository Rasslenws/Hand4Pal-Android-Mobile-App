package com.example.hand4pal_android_mobile_app.features.donation.domain

import com.example.hand4pal_android_mobile_app.features.campaign.domain.DonationDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.MakeDonationRequest

interface DonationRepository {
    suspend fun getMyDonations(): Result<List<DonationHistory>>
    suspend fun makeDonation(request: MakeDonationRequest): Result<DonationDTO>
}

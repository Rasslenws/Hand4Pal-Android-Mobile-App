package com.example.hand4pal_android_mobile_app.features.donation.data

import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.domain.DonationDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.MakeDonationRequest
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationHistory
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationRepository

class DonationRepositoryImpl : DonationRepository {
    private val api = RetrofitClient.donationApi

    override suspend fun getMyDonations(): Result<List<DonationHistory>> {
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

    override suspend fun makeDonation(request: MakeDonationRequest): Result<DonationDTO> {
        return try {
            val response = api.makeDonation(request)
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

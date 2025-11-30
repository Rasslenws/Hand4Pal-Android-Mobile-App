package com.example.hand4pal_android_mobile_app.features.donation.data

import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationHistory
import retrofit2.Response
import retrofit2.http.GET

interface DonationApi {
    @GET("donations/my-donations")
    suspend fun getMyDonations(): Response<List<DonationHistory>>
}

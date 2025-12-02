package com.example.hand4pal_android_mobile_app.features.donation.data

import com.example.hand4pal_android_mobile_app.features.campaign.domain.DonationDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.MakeDonationRequest
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationHistory
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DonationApi {
    @GET("donations/my-donations")
    suspend fun getMyDonations(): Response<List<DonationHistory>>

    @POST("donations")
    suspend fun makeDonation(@Body request: MakeDonationRequest): Response<DonationDTO>
}

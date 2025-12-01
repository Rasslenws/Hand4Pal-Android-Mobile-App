package com.example.hand4pal_android_mobile_app.features.campaign.data

import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Retrofit API interface for Campaign endpoints
 * Follows the same pattern as AuthApi and ProfileApi
 */
interface CampaignApi {
    
    /**
     * GET /campaigns/active/with-details
     * Fetches all active campaigns with complete details including comments and donations
     */
    @GET("campaigns/active/with-details")
    suspend fun getActiveCampaigns(): Response<List<CampaignDTO>>
    
    /**
     * GET /campaigns/{campaignId}/with-details
     * Fetches a single campaign by ID with complete details
     */
    @GET("campaigns/{campaignId}/with-details")
    suspend fun getCampaignById(
        @Path("campaignId") campaignId: Long
    ): Response<CampaignDTO>
}

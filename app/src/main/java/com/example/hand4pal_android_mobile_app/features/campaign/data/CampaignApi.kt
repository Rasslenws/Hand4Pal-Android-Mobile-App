package com.example.hand4pal_android_mobile_app.features.campaign.data

import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CreateCampaignRequest
import com.example.hand4pal_android_mobile_app.features.campaign.domain.DonationDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.MakeDonationRequest
import com.example.hand4pal_android_mobile_app.features.campaign.domain.UpdateCampaignRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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

    /**
     * GET /campaigns/my-campaigns/with-association
     * Fetches all campaigns belonging to the authenticated association
     */
    @GET("campaigns/my-campaigns/with-association")
    suspend fun getMyCampaigns(): Response<List<CampaignDTO>>

    /**
     * GET /campaigns/{campaignId}/with-association
     * Fetches a single campaign with association details
     */
    @GET("campaigns/{campaignId}/with-association")
    suspend fun getCampaignWithAssociation(
        @Path("campaignId") campaignId: Long
    ): Response<CampaignDTO>

    /**
     * POST /campaigns
     * Creates a new campaign
     */
    @POST("campaigns")
    suspend fun createCampaign(
        @Body request: CreateCampaignRequest
    ): Response<CampaignDTO>

    /**
     * PUT /campaigns/{campaignId}
     * Updates an existing campaign
     */
    @PUT("campaigns/{campaignId}")
    suspend fun updateCampaign(
        @Path("campaignId") campaignId: Long,
        @Body request: UpdateCampaignRequest
    ): Response<CampaignDTO>

    /**
     * POST /donations
     * Makes a donation to a campaign
     */
    @POST("donations")
    suspend fun makeDonation(
        @Body request: MakeDonationRequest
    ): Response<DonationDTO>
}

package com.example.hand4pal_android_mobile_app.features.campaign.domain

/**
 * Repository interface for Campaign feature
 * Follows the same pattern as AuthRepository and ProfileRepository
 * 
 * Defines the contract for data operations
 * Implementation is in the data layer
 */
interface CampaignRepository {
    /**
     * Fetch all active campaigns
     * @return Result wrapping list of campaigns or error
     */
    suspend fun getActiveCampaigns(): Result<List<CampaignDTO>>
    
    /**
     * Fetch a single campaign by ID
     * @param campaignId the campaign ID
     * @return Result wrapping campaign or error
     */
    suspend fun getCampaignById(campaignId: Long): Result<CampaignDTO>

    /**
     * Fetch all campaigns belonging to the authenticated association
     * @return Result wrapping list of campaigns or error
     */
    suspend fun getMyCampaigns(): Result<List<CampaignDTO>>

    /**
     * Fetch a single campaign with association details
     * @param campaignId the campaign ID
     * @return Result wrapping campaign or error
     */
    suspend fun getCampaignWithAssociation(campaignId: Long): Result<CampaignDTO>

    /**
     * Create a new campaign
     * @param request the campaign creation request
     * @return Result wrapping created campaign or error
     */
    suspend fun createCampaign(request: CreateCampaignRequest): Result<CampaignDTO>

    /**
     * Update an existing campaign
     * @param campaignId the campaign ID
     * @param request the campaign update request
     * @return Result wrapping updated campaign or error
     */
    suspend fun updateCampaign(campaignId: Long, request: UpdateCampaignRequest): Result<CampaignDTO>

    /**
     * Make a donation to a campaign
     * @param request the donation request
     * @return Result wrapping donation or error
     */
    suspend fun makeDonation(request: MakeDonationRequest): Result<DonationDTO>
}

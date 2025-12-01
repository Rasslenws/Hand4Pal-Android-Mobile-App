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
}

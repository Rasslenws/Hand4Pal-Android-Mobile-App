package com.example.hand4pal_android_mobile_app.features.campaign.domain

/**
 * Use case for fetching active campaigns
 * Follows Clean Architecture principle: business logic in use cases
 * 
 * In this simple case, it delegates to repository,
 * but could add validation, caching, or transformation logic
 */
class GetCampaignsUseCase(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(): Result<List<CampaignDTO>> {
        return repository.getActiveCampaigns()
    }
}

/**
 * Use case for fetching campaign details
 */
class GetCampaignDetailsUseCase(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(campaignId: Long): Result<CampaignDTO> {
        return repository.getCampaignById(campaignId)
    }
}

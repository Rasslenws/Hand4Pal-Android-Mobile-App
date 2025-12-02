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

/**
 * Use case for fetching association's campaigns
 */
class GetMyCampaignsUseCase(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(): Result<List<CampaignDTO>> {
        return repository.getMyCampaigns()
    }
}

/**
 * Use case for filtering campaigns by status
 */
class FilterMyCampaignsUseCase {
    operator fun invoke(campaigns: List<CampaignDTO>, status: CampaignStatus): List<CampaignDTO> {
        if (status == CampaignStatus.ALL) {
            return campaigns
        }
        return campaigns.filter { it.status.equals(status.value, ignoreCase = true) }
    }
}

/**
 * Use case for creating a campaign
 */
class CreateCampaignUseCase(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(request: CreateCampaignRequest): Result<CampaignDTO> {
        return repository.createCampaign(request)
    }
}

/**
 * Use case for updating a campaign
 */
class UpdateCampaignUseCase(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(campaignId: Long, request: UpdateCampaignRequest): Result<CampaignDTO> {
        return repository.updateCampaign(campaignId, request)
    }
}

/**
 * Use case for fetching campaign with association details
 */
class GetCampaignWithAssociationUseCase(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(campaignId: Long): Result<CampaignDTO> {
        return repository.getCampaignWithAssociation(campaignId)
    }
}

/**
 * Use case for making a donation
 */
class MakeDonationUseCase(
    private val repository: CampaignRepository
) {
    suspend operator fun invoke(request: MakeDonationRequest): Result<DonationDTO> {
        return repository.makeDonation(request)
    }
}

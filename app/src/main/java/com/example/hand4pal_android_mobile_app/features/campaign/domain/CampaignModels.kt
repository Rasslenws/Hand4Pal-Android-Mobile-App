package com.example.hand4pal_android_mobile_app.features.campaign.domain


/**
 * Domain models for Campaign feature
 * Matches backend Campaign and related DTOs
 */

// Main Campaign DTO that matches backend response
data class CampaignDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val category: String,
    val status: String,
    val targetAmount: Double,
    val collectedAmount: Double,
    val associationId: Long,
    val associationName: String? = null, // From /with-association endpoints
    val createdAt: String?,
    val updatedAt: String?,
    val endDate: String?,
    val startDate: String? = null, // May be returned from backend
    val imageURL: String?,
    val comments: List<CommentDTO>? = emptyList(),
    val donations: List<DonationDTO>? = emptyList()
)

// Comment DTO
data class CommentDTO(
    val id: Long?,
    val userId: Long,
    val userName: String?,
    val campaignId: Long,
    val content: String,
    val createdAt: String?
)

// Donation DTO
data class DonationDTO(
    val id: Long?,
    val userId: Long,
    val userName: String?,
    val campaignId: Long,
    val amount: Double,
    val isAnonymous: Boolean,
    val wish: String?,
    val createdAt: String?
)

// UI State for Campaign List
sealed class CampaignListUiState {
    object Idle : CampaignListUiState()
    object Loading : CampaignListUiState()
    data class Success(val campaigns: List<CampaignDTO>) : CampaignListUiState()
    data class Error(val message: String) : CampaignListUiState()
}

// UI State for Campaign Detail
sealed class CampaignDetailUiState {
    object Idle : CampaignDetailUiState()
    object Loading : CampaignDetailUiState()
    data class Success(val campaign: CampaignDTO) : CampaignDetailUiState()
    data class Error(val message: String) : CampaignDetailUiState()
}

// Request model for creating a campaign
// Backend will set: associationId (from token), status, collectedAmount, createdAt, updatedAt
data class CreateCampaignRequest(
    val title: String,
    val description: String,
    val targetAmount: Double,
    val category: String,
    val imageURL: String? = null,
    val startDate: String? = null,   // Optional - Format: yyyy-MM-ddT00:00:00
    val endDate: String? = null      // Optional - Format: yyyy-MM-ddT00:00:00
)

// Request model for updating a campaign
data class UpdateCampaignRequest(
    val title: String,
    val description: String,
    val targetAmount: Double,
    val category: String,
    val imageURL: String?,
    val endDate: String? = null
)

// UI State for Campaign Form (Create/Update)
sealed class CampaignFormUiState {
    object Idle : CampaignFormUiState()
    object Loading : CampaignFormUiState()
    object Success : CampaignFormUiState()
    data class Error(val message: String) : CampaignFormUiState()
}

// Campaign status enum
// Note: ACTIVE filters campaigns with status "APPROVED" from backend
enum class CampaignStatus(val value: String) {
    ALL("ALL"),
    ACTIVE("APPROVED"),  // Maps to backend's APPROVED status
    PENDING("PENDING"),
    REJECTED("REJECTED"),
    COMPLETED("COMPLETED")
}

// Request model for making a donation
data class MakeDonationRequest(
    val campaignId: Long,
    val amount: Double,
    val isAnonymous: Boolean,
    val wish: String?,
    val currency: String
)

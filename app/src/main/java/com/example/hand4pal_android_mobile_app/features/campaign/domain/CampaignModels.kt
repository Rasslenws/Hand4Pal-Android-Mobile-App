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
    val createdAt: String?,
    val updatedAt: String?,
    val endDate: String?,
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

package com.example.hand4pal_android_mobile_app.features.campaign.domain

// Request model for creating a comment
data class CommentRequestDTO(
    val campaignId: Long,
    val content: String
)

// Response model for a comment
data class CommentResponseDTO(
    val id: Long,
    val userId: Long,
    val userName: String?,
    val campaignId: Long,
    val content: String,
    val createdAt: String?
)

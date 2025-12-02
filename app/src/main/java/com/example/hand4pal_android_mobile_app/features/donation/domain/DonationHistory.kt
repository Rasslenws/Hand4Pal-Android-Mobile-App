package com.example.hand4pal_android_mobile_app.features.donation.domain

data class DonationHistory(
    val id: Long,
    val amount: Double,
    val currency: String,
    val donationDate: String,
    val campaign: CampaignInfo?
)

data class CampaignInfo(
    val id: Long,
    val title: String,
    val associationId: Long,
    val targetAmount: Double,
    val collectedAmount: Double
)

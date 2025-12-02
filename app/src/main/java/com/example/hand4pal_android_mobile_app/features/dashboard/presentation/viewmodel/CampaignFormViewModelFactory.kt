package com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CreateCampaignUseCase
import com.example.hand4pal_android_mobile_app.features.campaign.domain.UpdateCampaignUseCase

/**
 * Factory for creating CampaignFormViewModel
 * Follows the same pattern as AuthViewModelFactory
 */
class CampaignFormViewModelFactory(
    private val createCampaignUseCase: CreateCampaignUseCase,
    private val updateCampaignUseCase: UpdateCampaignUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampaignFormViewModel::class.java)) {
            return CampaignFormViewModel(
                createCampaignUseCase,
                updateCampaignUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


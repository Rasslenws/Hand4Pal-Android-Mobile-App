package com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hand4pal_android_mobile_app.features.campaign.domain.FilterMyCampaignsUseCase
import com.example.hand4pal_android_mobile_app.features.campaign.domain.GetMyCampaignsUseCase

class AssociationDashboardViewModelFactory(
    private val getMyCampaignsUseCase: GetMyCampaignsUseCase,
    private val filterMyCampaignsUseCase: FilterMyCampaignsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssociationDashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssociationDashboardViewModel(getMyCampaignsUseCase, filterMyCampaignsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


package com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.campaign.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Campaign Form (Create/Update)
 * Follows MVVM pattern used in Auth features
 */
class CampaignFormViewModel(
    private val createCampaignUseCase: CreateCampaignUseCase,
    private val updateCampaignUseCase: UpdateCampaignUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignFormUiState>(CampaignFormUiState.Idle)
    val uiState: StateFlow<CampaignFormUiState> = _uiState

    private val _existingCampaign = MutableStateFlow<CampaignDTO?>(null)
    val existingCampaign: StateFlow<CampaignDTO?> = _existingCampaign

    fun setExistingCampaign(campaign: CampaignDTO?) {
        _existingCampaign.value = campaign
    }

    fun isUpdateMode(): Boolean = _existingCampaign.value != null

    fun createCampaign(
        title: String,
        description: String,
        targetAmount: Double,
        startDate: String?,
        endDate: String?,
        category: String,
        imageURL: String?
    ) {
        viewModelScope.launch {
            _uiState.value = CampaignFormUiState.Loading
            val request = CreateCampaignRequest(
                title = title,
                description = description,
                targetAmount = targetAmount,
                category = category,
                imageURL = imageURL,
                startDate = startDate,
                endDate = endDate
            )
            createCampaignUseCase(request)
                .onSuccess {
                    _uiState.value = CampaignFormUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = CampaignFormUiState.Error(
                        error.message ?: "Failed to create campaign"
                    )
                }
        }
    }

    fun updateCampaign(
        campaignId: Long,
        title: String,
        description: String,
        targetAmount: Double,
        endDate: String?,
        category: String,
        imageURL: String?
    ) {
        viewModelScope.launch {
            _uiState.value = CampaignFormUiState.Loading
            val request = UpdateCampaignRequest(
                title = title,
                description = description,
                targetAmount = targetAmount,
                category = category,
                imageURL = imageURL,
                endDate = endDate
            )
            updateCampaignUseCase(campaignId, request)
                .onSuccess {
                    _uiState.value = CampaignFormUiState.Success
                }
                .onFailure { error ->
                    _uiState.value = CampaignFormUiState.Error(
                        error.message ?: "Failed to update campaign"
                    )
                }
        }
    }

    fun resetState() {
        _uiState.value = CampaignFormUiState.Idle
    }
}


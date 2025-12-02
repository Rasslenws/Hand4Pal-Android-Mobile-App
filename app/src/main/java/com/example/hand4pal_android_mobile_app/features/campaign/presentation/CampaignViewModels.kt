package com.example.hand4pal_android_mobile_app.features.campaign.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.campaign.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Campaign List screen
 * Follows the exact same pattern as AuthViewModel and ProfileViewModel
 * 
 * Responsibilities:
 * - Expose StateFlow for UI state
 * - Call use cases
 * - Update state based on results
 */
class CampaignListViewModel(
    private val getCampaignsUseCase: GetCampaignsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignListUiState>(CampaignListUiState.Idle)
    val uiState: StateFlow<CampaignListUiState> = _uiState
    
    /**
     * Load active campaigns
     * Updates UI state based on result
     */
    fun loadCampaigns() {
        viewModelScope.launch {
            _uiState.value = CampaignListUiState.Loading
            getCampaignsUseCase()
                .onSuccess { campaigns ->
                    _uiState.value = CampaignListUiState.Success(campaigns)
                }
                .onFailure { error ->
                    _uiState.value = CampaignListUiState.Error(
                        error.message ?: "Failed to load campaigns"
                    )
                }
        }
    }
    
    fun resetState() {
        _uiState.value = CampaignListUiState.Idle
    }
}

/**
 * ViewModel for Campaign Detail screen
 */
class CampaignDetailViewModel(
    private val getCampaignDetailsUseCase: GetCampaignDetailsUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CampaignDetailUiState>(CampaignDetailUiState.Idle)
    val uiState: StateFlow<CampaignDetailUiState> = _uiState
    
    /**
     * Load campaign details by ID
     * Updates UI state based on result
     */
    fun loadCampaignDetails(campaignId: Long) {
        viewModelScope.launch {
            _uiState.value = CampaignDetailUiState.Loading
            getCampaignDetailsUseCase(campaignId)
                .onSuccess { campaign ->
                    _uiState.value = CampaignDetailUiState.Success(campaign)
                }
                .onFailure { error ->
                    _uiState.value = CampaignDetailUiState.Error(
                        error.message ?: "Failed to load campaign details"
                    )
                }
        }
    }
    
    fun resetState() {
        _uiState.value = CampaignDetailUiState.Idle
    }
}

/**
 * ViewModelFactory for CampaignListViewModel
 * Follows the same pattern as ProfileViewModelFactory
 */
class CampaignListViewModelFactory(
    private val getCampaignsUseCase: GetCampaignsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampaignListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampaignListViewModel(getCampaignsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * ViewModelFactory for CampaignDetailViewModel
 */
class CampaignDetailViewModelFactory(
    private val getCampaignDetailsUseCase: GetCampaignDetailsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampaignDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampaignDetailViewModel(getCampaignDetailsUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

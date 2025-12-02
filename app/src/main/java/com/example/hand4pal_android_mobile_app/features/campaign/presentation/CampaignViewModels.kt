package com.example.hand4pal_android_mobile_app.features.campaign.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.campaign.domain.*
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CampaignListViewModel(
    private val getCampaignsUseCase: GetCampaignsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignListUiState>(CampaignListUiState.Idle)
    val uiState: StateFlow<CampaignListUiState> = _uiState
    

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

class CampaignDetailViewModel(
    private val getCampaignDetailsUseCase: GetCampaignDetailsUseCase,
    private val donationRepository: DonationRepository,
    private val commentRepository: CommentRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<CampaignDetailUiState>(CampaignDetailUiState.Idle)
    val uiState: StateFlow<CampaignDetailUiState> = _uiState

    private val _donationState = MutableStateFlow<DonationUiState>(DonationUiState.Idle)
    val donationState: StateFlow<DonationUiState> = _donationState
    
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

    fun makeDonationAndComment(donationRequest: MakeDonationRequest) {
        viewModelScope.launch {
            _donationState.value = DonationUiState.Loading
            donationRepository.makeDonation(donationRequest)
                .onSuccess { donation ->
                    _donationState.value = DonationUiState.Success(donation)
                    // If the donation wish is not blank, create a comment
                    if (!donationRequest.wish.isNullOrBlank()) {
                        val commentRequest = CommentRequestDTO(
                            campaignId = donationRequest.campaignId,
                            content = donationRequest.wish
                        )
                        commentRepository.createComment(commentRequest)
                            .onSuccess {
                                Log.d("ViewModel", "Comment created successfully")
                            }
                            .onFailure {
                                Log.e("ViewModel", "Failed to create comment: ${it.message}")
                            }
                    }
                }
                .onFailure { error ->
                    _donationState.value = DonationUiState.Error(
                        error.message ?: "Failed to make donation"
                    )
                }
        }
    }
    
    fun resetState() {
        _uiState.value = CampaignDetailUiState.Idle
        _donationState.value = DonationUiState.Idle
    }
}

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

class CampaignDetailViewModelFactory(
    private val getCampaignDetailsUseCase: GetCampaignDetailsUseCase,
    private val donationRepository: DonationRepository,
    private val commentRepository: CommentRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CampaignDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CampaignDetailViewModel(getCampaignDetailsUseCase, donationRepository, commentRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

sealed class DonationUiState {
    object Idle : DonationUiState()
    object Loading : DonationUiState()
    data class Success(val donation: DonationDTO) : DonationUiState()
    data class Error(val message: String) : DonationUiState()
}
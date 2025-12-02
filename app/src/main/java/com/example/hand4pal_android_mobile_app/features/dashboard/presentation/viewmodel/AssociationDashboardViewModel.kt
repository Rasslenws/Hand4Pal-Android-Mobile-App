package com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.campaign.domain.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Association Dashboard
 * - Loads association's campaigns via GetMyCampaignsUseCase
 * - Applies client-side filtering using FilterMyCampaignsUseCase
 */
class AssociationDashboardViewModel(
    private val getMyCampaignsUseCase: GetMyCampaignsUseCase,
    private val filterMyCampaignsUseCase: FilterMyCampaignsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CampaignListUiState>(CampaignListUiState.Idle)
    val uiState: StateFlow<CampaignListUiState> = _uiState.asStateFlow()

    // Cache the full list for client-side filtering
    private var allCampaigns: List<CampaignDTO> = emptyList()
    private var currentFilter: CampaignStatus = CampaignStatus.ALL

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = CampaignListUiState.Loading
            try {
                val result = getMyCampaignsUseCase()
                result.onSuccess { list ->
                    allCampaigns = list
                    applyFilter()
                }.onFailure { t ->
                    _uiState.value = CampaignListUiState.Error(t.message ?: "Erreur réseau")
                }
            } catch (t: Throwable) {
                _uiState.value = CampaignListUiState.Error(t.message ?: "Erreur réseau")
            }
        }
    }

    private fun applyFilter() {
        val filtered = filterMyCampaignsUseCase.invoke(allCampaigns, currentFilter)
        _uiState.value = CampaignListUiState.Success(filtered)
    }

    fun filterByStatus(status: CampaignStatus) {
        currentFilter = status
        applyFilter()
    }
}


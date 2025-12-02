package com.example.hand4pal_android_mobile_app.features.donation.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.core.EventBus
import com.example.hand4pal_android_mobile_app.core.DonationMadeEvent
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationHistory
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DonationViewModel(private val repository: DonationRepository) : ViewModel() {

    private val _donations = MutableLiveData<List<DonationHistory>>()
    val donations: LiveData<List<DonationHistory>> get() = _donations

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    init {
        viewModelScope.launch {
            EventBus.events.collectLatest {
                if (it is DonationMadeEvent) {
                    loadDonations()
                }
            }
        }
    }

    fun loadDonations() {
        _isLoading.value = true

        viewModelScope.launch {
            val result = repository.getMyDonations()

            result.onSuccess { list ->
                _donations.value = list
                _error.value = null
            }

            result.onFailure { e ->
                _error.value = e.message
            }

            _isLoading.value = false
        }
    }
}

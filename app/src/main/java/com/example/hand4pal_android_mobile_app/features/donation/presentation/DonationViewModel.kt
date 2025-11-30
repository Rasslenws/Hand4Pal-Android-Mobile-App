package com.example.hand4pal_android_mobile_app.features.donation.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hand4pal_android_mobile_app.features.donation.data.DonationRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationHistory
import kotlinx.coroutines.launch

class DonationViewModel : ViewModel() {

    private val repository = DonationRepositoryImpl()

    private val _donations = MutableLiveData<List<DonationHistory>>()
    val donations: LiveData<List<DonationHistory>> get() = _donations

    // ---> AJOUTER CES LIGNES <---
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    // ---------------------------

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun loadDonations() {
        _isLoading.value = true // Début chargement

        viewModelScope.launch {
            val result = repository.getMyDonations()

            result.onSuccess { list ->
                _donations.value = list
                _error.value = null
            }

            result.onFailure { e ->
                _error.value = e.message
            }

            _isLoading.value = false // Fin chargement
        }
    }
}

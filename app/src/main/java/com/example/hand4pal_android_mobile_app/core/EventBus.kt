package com.example.hand4pal_android_mobile_app.core

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object EventBus {
    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    suspend fun post(event: Any) {
        _events.emit(event)
    }
}

data class DonationMadeEvent(val campaignId: Long)

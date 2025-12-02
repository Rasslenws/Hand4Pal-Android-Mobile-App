package com.example.hand4pal_android_mobile_app.features.campaign.data

import com.example.hand4pal_android_mobile_app.features.campaign.domain.CommentRequestDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CommentResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface CommentApi {
    @POST("comments")
    suspend fun createComment(@Body request: CommentRequestDTO): Response<CommentResponseDTO>
}

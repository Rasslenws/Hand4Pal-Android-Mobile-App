package com.example.hand4pal_android_mobile_app.features.campaign.domain

interface CommentRepository {
    suspend fun createComment(request: CommentRequestDTO): Result<CommentResponseDTO>
}

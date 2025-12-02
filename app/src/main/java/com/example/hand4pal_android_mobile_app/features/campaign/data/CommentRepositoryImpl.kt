package com.example.hand4pal_android_mobile_app.features.campaign.data

import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CommentRepository
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CommentRequestDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CommentResponseDTO

class CommentRepositoryImpl : CommentRepository {
    private val api = RetrofitClient.retrofit.create(CommentApi::class.java)

    override suspend fun createComment(request: CommentRequestDTO): Result<CommentResponseDTO> {
        return try {
            val response = api.createComment(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Erreur ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

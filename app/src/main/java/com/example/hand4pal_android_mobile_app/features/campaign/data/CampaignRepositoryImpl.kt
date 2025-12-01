package com.example.hand4pal_android_mobile_app.features.campaign.data

import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignRepository
import com.google.gson.Gson

/**
 * Implementation of CampaignRepository
 * Follows the exact same pattern as AuthRepositoryImpl and ProfileRepositoryImpl
 * 
 * Responsibilities:
 * - Call Retrofit API
 * - Wrap results in Result<T>
 * - Handle errors consistently with Gson parsing
 */
class CampaignRepositoryImpl(
    private val api: CampaignApi
) : CampaignRepository {
    
    override suspend fun getActiveCampaigns(): Result<List<CampaignDTO>> {
        return try {
            val response = api.getActiveCampaigns()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to load campaigns"
                } catch (e: Exception) {
                    "Failed to load campaigns"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
    
    override suspend fun getCampaignById(campaignId: Long): Result<CampaignDTO> {
        return try {
            val response = api.getCampaignById(campaignId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to load campaign details"
                } catch (e: Exception) {
                    "Campaign not found"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}

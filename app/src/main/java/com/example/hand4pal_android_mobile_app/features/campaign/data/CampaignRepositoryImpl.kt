package com.example.hand4pal_android_mobile_app.features.campaign.data

import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignRepository
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CreateCampaignRequest
import com.example.hand4pal_android_mobile_app.features.campaign.domain.UpdateCampaignRequest
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

    override suspend fun getMyCampaigns(): Result<List<CampaignDTO>> {
        return try {
            val response = api.getMyCampaigns()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to load your campaigns"
                } catch (e: Exception) {
                    "Failed to load your campaigns"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun getCampaignWithAssociation(campaignId: Long): Result<CampaignDTO> {
        return try {
            val response = api.getCampaignWithAssociation(campaignId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to load campaign"
                } catch (e: Exception) {
                    "Campaign not found"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun createCampaign(request: CreateCampaignRequest): Result<CampaignDTO> {
        return try {
            val response = api.createCampaign(request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                android.util.Log.e("CampaignRepository", "Create campaign failed: $errorBody")
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    // Try to extract 'error' or 'message' field
                    (errorMap["error"]?.toString() ?: errorMap["message"]?.toString())
                        ?: "Failed to create campaign (${response.code()})"
                } catch (e: Exception) {
                    errorBody ?: "Failed to create campaign"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            android.util.Log.e("CampaignRepository", "Network error creating campaign", e)
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }

    override suspend fun updateCampaign(campaignId: Long, request: UpdateCampaignRequest): Result<CampaignDTO> {
        return try {
            val response = api.updateCampaign(campaignId, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = try {
                    val errorMap = Gson().fromJson(errorBody, Map::class.java)
                    errorMap["message"]?.toString() ?: "Failed to update campaign"
                } catch (e: Exception) {
                    "Failed to update campaign"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}"))
        }
    }
}

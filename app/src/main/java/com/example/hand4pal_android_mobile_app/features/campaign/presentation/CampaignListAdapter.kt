package com.example.hand4pal_android_mobile_app.features.campaign.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDTO
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * RecyclerView Adapter for Campaign List
 * Follows the same pattern as DonationHistoryAdapter
 * 
 * Displays campaigns in a card-based layout
 */
class CampaignListAdapter(
    private val onCampaignClick: (CampaignDTO) -> Unit
) : RecyclerView.Adapter<CampaignListAdapter.CampaignViewHolder>() {
    
    private var campaigns: List<CampaignDTO> = emptyList()
    
    fun submitList(newCampaigns: List<CampaignDTO>) {
        campaigns = newCampaigns
        notifyDataSetChanged()
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campaign_card, parent, false)
        return CampaignViewHolder(view)
    }
    
    override fun onBindViewHolder(holder: CampaignViewHolder, position: Int) {
        holder.bind(campaigns[position])
    }
    
    override fun getItemCount(): Int = campaigns.size
    
    inner class CampaignViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivCampaignImage: ImageView = itemView.findViewById(R.id.ivCampaignImage)
        private val tvCampaignTitle: TextView = itemView.findViewById(R.id.tvCampaignTitle)
        private val tvCampaignCategory: TextView = itemView.findViewById(R.id.tvCampaignCategory)
        private val tvCollectedAmount: TextView = itemView.findViewById(R.id.tvCollectedAmount)
        private val tvTargetAmount: TextView = itemView.findViewById(R.id.tvTargetAmount)
        private val tvDaysLeft: TextView = itemView.findViewById(R.id.tvDaysLeft)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        
        fun bind(campaign: CampaignDTO) {
            tvCampaignTitle.text = campaign.title
            tvCampaignCategory.text = campaign.category
            
            // Format amounts
            @Suppress("DEPRECATION")
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("fr", "TN"))
            tvCollectedAmount.text = currencyFormat.format(campaign.collectedAmount)
            tvTargetAmount.text = currencyFormat.format(campaign.targetAmount)
            
            // Calculate progress percentage
            val progress = if (campaign.targetAmount > 0) {
                ((campaign.collectedAmount / campaign.targetAmount) * 100).toInt()
            } else {
                0
            }
            progressBar.progress = progress
            
            // Calculate days left
            campaign.endDate?.let { endDateString ->
                try {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    val endDate = dateFormat.parse(endDateString)
                    endDate?.let {
                        val currentDate = Date()
                        val diffInMillis = it.time - currentDate.time
                        val daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                        tvDaysLeft.text = if (daysLeft > 0) {
                            "$daysLeft days left"
                        } else {
                            "Ended"
                        }
                    } ?: run {
                        tvDaysLeft.text = "N/A"
                    }
                } catch (_: Exception) {
                    tvDaysLeft.text = "N/A"
                }
            } ?: run {
                tvDaysLeft.text = "N/A"
            }
            
            // Set placeholder image (can be enhanced with image loading library like Glide/Coil)
            ivCampaignImage.setImageResource(R.drawable.banner_placeholder)
            
            // Handle click
            itemView.setOnClickListener {
                onCampaignClick(campaign)
            }
        }
    }
}

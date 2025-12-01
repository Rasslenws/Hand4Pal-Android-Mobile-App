package com.example.hand4pal_android_mobile_app.features.dashboard.presentation.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDTO
import com.google.android.material.button.MaterialButton
import java.text.NumberFormat
import java.util.Locale

/**
 * RecyclerView Adapter for Campaign Dashboard Cards
 * Includes Edit and Delete buttons for association's own campaigns
 */
class CampaignDashboardAdapter(
    private val onViewClick: (CampaignDTO) -> Unit,
    private val onEditClick: (CampaignDTO) -> Unit,
    private val onDeleteClick: (CampaignDTO) -> Unit
) : RecyclerView.Adapter<CampaignDashboardAdapter.CampaignViewHolder>() {

    private var campaigns: List<CampaignDTO> = emptyList()

    fun submitList(newCampaigns: List<CampaignDTO>) {
        campaigns = newCampaigns
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampaignViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_campaign_dashboard_card, parent, false)
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
        private val tvProgressPercentage: TextView = itemView.findViewById(R.id.tvProgressPercentage)
        private val tvStatusBadge: TextView = itemView.findViewById(R.id.tvStatusBadge)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progressBar)
        private val btnViewDetails: MaterialButton = itemView.findViewById(R.id.btnViewDetails)
        private val btnEdit: MaterialButton = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDelete)

        fun bind(campaign: CampaignDTO) {
            // Title and Category
            tvCampaignTitle.text = campaign.title
            tvCampaignCategory.text = campaign.category

            // Format amounts
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
            tvProgressPercentage.text = "$progress%"

            // Status Badge
            tvStatusBadge.text = campaign.status
            tvStatusBadge.setBackgroundResource(getStatusBackground(campaign.status))
            tvStatusBadge.setTextColor(Color.WHITE)

            // Set placeholder image
            ivCampaignImage.setImageResource(R.drawable.banner_placeholder)

            // Button click listeners
            btnViewDetails.setOnClickListener { onViewClick(campaign) }
            btnEdit.setOnClickListener { onEditClick(campaign) }
            btnDelete.setOnClickListener { onDeleteClick(campaign) }

            // Disable edit/delete for approved/active campaigns (optional)
            // You can customize this logic based on your requirements
            when (campaign.status.uppercase()) {
                "REJECTED" -> {
                    btnEdit.isEnabled = false
                    btnEdit.alpha = 0.5f
                }
                else -> {
                    btnEdit.isEnabled = true
                    btnEdit.alpha = 1.0f
                }
            }
        }

        private fun getStatusBackground(status: String): Int {
            return when (status.uppercase()) {
                "PENDING" -> R.drawable.bg_status_pending
                "APPROVED", "ACTIVE" -> R.drawable.bg_status_approved
                "REJECTED" -> R.drawable.bg_status_rejected
                "COMPLETED" -> R.drawable.bg_status_completed
                else -> R.drawable.bg_status_badge
            }
        }
    }
}


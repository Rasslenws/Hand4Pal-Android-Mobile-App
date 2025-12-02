package com.example.hand4pal_android_mobile_app.features.campaign.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.campaign.domain.DonationDTO
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class DonationAdapter : ListAdapter<DonationDTO, DonationAdapter.DonationViewHolder>(DonationDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_donation, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = getItem(position)
        holder.bind(donation)
    }

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val donorNameTextView: TextView = itemView.findViewById(R.id.donor_name)
        private val donationAmountTextView: TextView = itemView.findViewById(R.id.donation_amount)
        private val donationTimeTextView: TextView = itemView.findViewById(R.id.donation_time)

        fun bind(donation: DonationDTO) {
            // Set Donor Name (or Anonymous)
            donorNameTextView.text = if (donation.isAnonymous) {
                "Anonymous"
            } else {
                donation.userName ?: "Unknown Donor"
            }

            // Set Donation Amount with currency formatting
            val currencyFormat = NumberFormat.getNumberInstance(Locale("fr", "TN")) // 1 234,567 style
            val formattedAmount = currencyFormat.format(donation.amount)
            donationAmountTextView.text = "$formattedAmount DT"


            // Set Relative Time
            donationTimeTextView.text = calculateTimeDiff(donation.createdAt)
        }

        private fun calculateTimeDiff(isoDate: String?): String {
            if (isoDate.isNullOrBlank()) return ""

            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val donationDate = sdf.parse(isoDate)
                val now = Date()
                val diff = now.time - donationDate.time

                val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                val hours = TimeUnit.MILLISECONDS.toHours(diff)
                val days = TimeUnit.MILLISECONDS.toDays(diff)

                when {
                    minutes < 1 -> "now"
                    minutes < 60 -> "${minutes}m"
                    hours < 24 -> "${hours}h"
                    else -> "${days}d"
                }
            } catch (e: Exception) {
                isoDate.take(10) // Fallback to date part
            }
        }
    }
}

class DonationDiffCallback : DiffUtil.ItemCallback<DonationDTO>() {
    override fun areItemsTheSame(oldItem: DonationDTO, newItem: DonationDTO): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DonationDTO, newItem: DonationDTO): Boolean {
        return oldItem == newItem
    }
}

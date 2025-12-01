package com.example.hand4pal_android_mobile_app.features.campaign.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.campaign.domain.DonationDTO
import java.text.SimpleDateFormat
import java.util.Locale

class DonationAdapter(private val donations: List<DonationDTO>) :
    RecyclerView.Adapter<DonationAdapter.DonationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DonationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation_card, parent, false)
        return DonationViewHolder(view)
    }

    override fun onBindViewHolder(holder: DonationViewHolder, position: Int) {
        val donation = donations[position]
        holder.bind(donation)
    }

    override fun getItemCount(): Int = donations.size

    class DonationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val donationAmountTextView: TextView = itemView.findViewById(R.id.txtRaised)
        private val donorNameTextView: TextView = itemView.findViewById(R.id.txtAuthor)
        private val donationDateTextView: TextView = itemView.findViewById(R.id.txtDate)

        fun bind(donation: DonationDTO) {
            donationAmountTextView.text = "${donation.amount}"
            donorNameTextView.text = donation.userName ?: if (donation.isAnonymous) "Don anonyme" else "Citoyen inconnu"
            donationDateTextView.text = formatDate(donation.createdAt)
        }

        private fun formatDate(rawDate: String?): String {
            if (rawDate.isNullOrBlank()) return ""
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val date = parser.parse(rawDate)
                formatter.format(date!!)
            } catch (_: Exception) {
                rawDate.take(10)
            }
        }
    }
}

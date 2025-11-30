package com.example.hand4pal_android_mobile_app.features.donation.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.donation.domain.DonationHistory
import java.text.SimpleDateFormat
import java.util.Locale

class DonationHistoryAdapter : RecyclerView.Adapter<DonationHistoryAdapter.ViewHolder>() {

    private var list: List<DonationHistory> = emptyList()

    fun submitList(newList: List<DonationHistory>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_donation_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Assurez-vous que ces IDs existent dans item_donation_card.xml
        private val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        private val txtAuthor: TextView = view.findViewById(R.id.txtAuthor)
        private val txtUserDonation: TextView = view.findViewById(R.id.txtUserDonation)
        private val txtDate: TextView = view.findViewById(R.id.txtDate)
        private val txtRaised: TextView = view.findViewById(R.id.txtRaised)
        private val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        fun bind(item: DonationHistory) {
            val campaign = item.campaign

            txtTitle.text = campaign?.title ?: "Unknown Campaign"
            txtAuthor.text = "Association #${campaign?.associationId}"
            txtUserDonation.text = "You have donated ${item.currency} ${item.amount}"

            // Format Date
            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
                val dateObj = parser.parse(item.donationDate)
                txtDate.text = formatter.format(dateObj!!)
            } catch (e: Exception) {
                txtDate.text = item.donationDate.take(10)
            }

            // Progress
            if (campaign != null && campaign.targetAmount > 0) {
                val progress = (campaign.collectedAmount / campaign.targetAmount) * 100
                progressBar.progress = progress.toInt()
                txtRaised.text = "${item.currency} ${campaign.collectedAmount}"
            }
        }
    }
}

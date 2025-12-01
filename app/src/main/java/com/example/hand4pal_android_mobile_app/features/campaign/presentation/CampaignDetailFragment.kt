package com.example.hand4pal_android_mobile_app.features.campaign.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.data.CampaignRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDTO
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignDetailUiState
import com.example.hand4pal_android_mobile_app.features.campaign.domain.GetCampaignDetailsUseCase
import com.example.hand4pal_android_mobile_app.features.campaign.presentation.adapters.CommentAdapter
import com.example.hand4pal_android_mobile_app.features.campaign.presentation.adapters.DonationAdapter
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * Fragment for displaying campaign details
 * Follows the same pattern as ProfileFragment
 * 
 * Receives campaignId via arguments bundle
 * Displays complete campaign information including comments and donations
 */
class CampaignDetailFragment : Fragment() {
    
    private val campaignId: Long by lazy {
        arguments?.getLong("campaignId") ?: throw IllegalArgumentException("campaignId is required")
    }

    private val viewModel: CampaignDetailViewModel by viewModels {
        RetrofitClient.init(requireContext().applicationContext)
        
        val repository = CampaignRepositoryImpl(RetrofitClient.campaignApi)
        val useCase = GetCampaignDetailsUseCase(repository)
        
        CampaignDetailViewModelFactory(useCase)
    }
    
    // Views
    private lateinit var toolbar: Toolbar
    private lateinit var progressBar: ProgressBar
    private lateinit var campaignImageView: ImageView
    private lateinit var campaignTitleTextView: TextView
    private lateinit var collectedAmountTextView: TextView
    private lateinit var targetAmountTextView: TextView
    private lateinit var daysLeftTextView: TextView
    private lateinit var campaignProgressBar: ProgressBar
    private lateinit var campaignerNameTextView: TextView
    private lateinit var campaignerAvatarImageView: ImageView
    private lateinit var verifiedBadgeImageView: ImageView
    private lateinit var campaignDescriptionTextView: TextView
    private lateinit var wishesCountTextView: TextView
    private lateinit var commentsRecyclerView: RecyclerView
    private lateinit var donationsCountTextView: TextView
    private lateinit var donationsRecyclerView: RecyclerView
    private lateinit var donateButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campaign_details, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupToolbar()
        observeUiState()
        
        // Load campaign details
        viewModel.loadCampaignDetails(campaignId)
    }
    
    private fun setupViews(view: View) {
        toolbar = view.findViewById(R.id.toolbar)
        progressBar = view.findViewById(R.id.progressBar)
        campaignImageView = view.findViewById(R.id.campaignImageView)
        campaignTitleTextView = view.findViewById(R.id.campaignTitleTextView)
        collectedAmountTextView = view.findViewById(R.id.collectedAmountTextView)
        targetAmountTextView = view.findViewById(R.id.targetAmountTextView)
        daysLeftTextView = view.findViewById(R.id.daysLeftTextView)
        campaignProgressBar = view.findViewById(R.id.campaignProgressBar)
        campaignerNameTextView = view.findViewById(R.id.campaignerNameTextView)
        campaignerAvatarImageView = view.findViewById(R.id.campaignerAvatarImageView)
        verifiedBadgeImageView = view.findViewById(R.id.verifiedBadgeImageView)
        campaignDescriptionTextView = view.findViewById(R.id.campaignDescriptionTextView)
        wishesCountTextView = view.findViewById(R.id.wishesCountTextView)
        commentsRecyclerView = view.findViewById(R.id.commentsRecyclerView)
        donationsCountTextView = view.findViewById(R.id.donationsCountTextView)
        donationsRecyclerView = view.findViewById(R.id.donationsRecyclerView)
        donateButton = view.findViewById(R.id.donateButton)
    }
    
    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is CampaignDetailUiState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is CampaignDetailUiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                    }
                    is CampaignDetailUiState.Success -> {
                        progressBar.visibility = View.GONE
                        populateCampaignDetails(state.campaign)
                    }
                    is CampaignDetailUiState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            state.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
    
    private fun populateCampaignDetails(campaign: CampaignDTO) {
        @Suppress("DEPRECATION")
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("fr", "TN"))
        
        // Basic info
        campaignTitleTextView.text = campaign.title
        collectedAmountTextView.text = currencyFormat.format(campaign.collectedAmount)
        targetAmountTextView.text = currencyFormat.format(campaign.targetAmount)
        
        // Progress
        val progress = if (campaign.targetAmount > 0) {
            ((campaign.collectedAmount / campaign.targetAmount) * 100).toInt()
        } else {
            0
        }
        campaignProgressBar.progress = progress
        
        // Days left
        campaign.endDate?.let { endDateString ->
            try {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val endDate = dateFormat.parse(endDateString)
                endDate?.let {
                    val currentDate = Date()
                    val diffInMillis = it.time - currentDate.time
                    val daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillis)
                    daysLeftTextView.text = if (daysLeft > 0) daysLeft.toString() else "0"
                } ?: run {
                    daysLeftTextView.text = "N/A"
                }
            } catch (_: Exception) {
                daysLeftTextView.text = "N/A"
            }
        } ?: run {
            daysLeftTextView.text = "N/A"
        }
        
        // Campaigner info (you can enhance this with association name from backend)
        campaignerNameTextView.text = getString(R.string.campaign_number, campaign.associationId)
        campaignerAvatarImageView.setImageResource(R.drawable.avatar1)
        verifiedBadgeImageView.visibility = View.VISIBLE
        
        // Description
        campaignDescriptionTextView.text = campaign.description ?: "No description available"

        // Header image
        val imageUrl = campaign.imageURL
        if (imageUrl.isNullOrBlank()) {
            campaignImageView.setImageResource(R.drawable.banner_placeholder)
        } else {
            Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.banner_placeholder)
                .error(R.drawable.banner_placeholder)
                .into(campaignImageView)
        }

        // Comments (wishes)
        val comments = campaign.comments ?: emptyList()
        wishesCountTextView.text = getString(R.string.count_plus, comments.size)
        commentsRecyclerView.adapter = CommentAdapter(comments)
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Donations
        val donations = campaign.donations ?: emptyList()
        donationsCountTextView.text = getString(R.string.count_plus, donations.size)
        donationsRecyclerView.adapter = DonationAdapter(donations)
        donationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Donate button
        donateButton.setOnClickListener {
            Toast.makeText(requireContext(), "Donate feature coming soon!", Toast.LENGTH_SHORT).show()
        }
    }
}

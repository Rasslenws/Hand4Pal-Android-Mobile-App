package com.example.hand4pal_android_mobile_app.features.campaign.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.EventBus
import com.example.hand4pal_android_mobile_app.core.DonationMadeEvent
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.data.CampaignRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.campaign.data.CommentRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.campaign.domain.*
import com.example.hand4pal_android_mobile_app.features.campaign.presentation.adapters.CommentAdapter
import com.example.hand4pal_android_mobile_app.features.campaign.presentation.adapters.DonationAdapter
import com.example.hand4pal_android_mobile_app.features.donation.data.DonationRepositoryImpl
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class CampaignDetailFragment : Fragment() {
    
    private val campaignId: Long by lazy {
        arguments?.getLong("campaignId") ?: throw IllegalArgumentException("campaignId is required")
    }

    private val viewModel: CampaignDetailViewModel by viewModels {
        RetrofitClient.init(requireContext().applicationContext)
        
        val campaignRepository = CampaignRepositoryImpl(RetrofitClient.campaignApi)
        val getCampaignDetailsUseCase = GetCampaignDetailsUseCase(campaignRepository)
        val donationRepository = DonationRepositoryImpl()
        val commentRepository = CommentRepositoryImpl()
        
        CampaignDetailViewModelFactory(getCampaignDetailsUseCase, donationRepository, commentRepository)
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
    private lateinit var donationAdapter: DonationAdapter

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
        
        // Initialize the adapter and set it to the RecyclerView
        donationAdapter = DonationAdapter()
        donationsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        donationsRecyclerView.adapter = donationAdapter
        
        observeUiState()
        observeDonationState()
        
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

    private fun observeDonationState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.donationState.collect { state ->
                when (state) {
                    is DonationUiState.Idle -> {}
                    is DonationUiState.Loading -> {}
                    is DonationUiState.Success -> {
                        Log.d("Donation", "Donation successful: ${state.donation}")
                        Toast.makeText(requireContext(), "Donation successful!", Toast.LENGTH_SHORT).show()
                        
                        // Post event to refresh donation history
                        lifecycleScope.launch {
                            EventBus.post(DonationMadeEvent(campaignId))
                        }

                        // Refresh campaign details
                        viewModel.loadCampaignDetails(campaignId)
                    }
                    is DonationUiState.Error -> {
                        Log.e("Donation", "Donation failed: ${state.message}")
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    
    private fun populateCampaignDetails(campaign: CampaignDTO) {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("fr", "TN"))
        
        campaignTitleTextView.text = campaign.title
        collectedAmountTextView.text = currencyFormat.format(campaign.collectedAmount)
        targetAmountTextView.text = currencyFormat.format(campaign.targetAmount)
        
        val progress = if (campaign.targetAmount > 0) {
            ((campaign.collectedAmount / campaign.targetAmount) * 100).toInt()
        } else {
            0
        }
        campaignProgressBar.progress = progress
        
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
        
        campaignerNameTextView.text = getString(R.string.campaign_number, campaign.associationId)
        campaignerAvatarImageView.setImageResource(R.drawable.avatar1)
        verifiedBadgeImageView.visibility = View.VISIBLE
        
        campaignDescriptionTextView.text = campaign.description ?: "No description available"

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

        val comments = campaign.comments ?: emptyList()
        wishesCountTextView.text = getString(R.string.count_plus, comments.size)
        commentsRecyclerView.adapter = CommentAdapter(comments)
        commentsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val donations = campaign.donations ?: emptyList()
        donationsCountTextView.text = getString(R.string.count_plus, donations.size)
        donationAdapter.submitList(donations)

        donateButton.setOnClickListener {
            showDonationDialog()
        }
    }

    private fun showDonationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_donate, null)
        val amountEditText = dialogView.findViewById<TextInputEditText>(R.id.amountEditText)
        val wishEditText = dialogView.findViewById<TextInputEditText>(R.id.wishEditText)

        builder.setView(dialogView)
            .setPositiveButton("Donate", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }

        val dialog = builder.create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener { 
                val amount = amountEditText.text.toString().toDoubleOrNull()
                if (amount == null || amount <= 0) {
                    amountEditText.error = "Please enter a valid amount"
                    return@setOnClickListener
                } else {
                    amountEditText.error = null
                }

                val wish = wishEditText.text.toString()
                val request = MakeDonationRequest(
                    campaignId = campaignId,
                    amount = amount,
                    isAnonymous = false, 
                    wish = wish,
                    currency = "DT"
                )
                Log.d("Donation", "Making donation with request: $request")
                viewModel.makeDonationAndComment(request)
                dialog.dismiss()
            }
        }

        dialog.show()
    }
}

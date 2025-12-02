package com.example.hand4pal_android_mobile_app.features.dashboard.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.data.CampaignRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.campaign.domain.*
import com.example.hand4pal_android_mobile_app.features.dashboard.presentation.adapters.CampaignDashboardAdapter
import com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel.AssociationDashboardViewModel
import com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel.AssociationDashboardViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

/**
 * Fragment for Association Dashboard
 * Shows list of campaigns belonging to the association with filter options
 */
class AssociationDashboardFragment : Fragment() {

    private lateinit var viewModel: AssociationDashboardViewModel
    private lateinit var rvCampaigns: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var llEmptyState: LinearLayout
    private lateinit var fabCreateCampaign: FloatingActionButton
    private lateinit var campaignAdapter: CampaignDashboardAdapter

    // Filter chips
    private lateinit var chipAll: Chip
    private lateinit var chipActive: Chip
    private lateinit var chipPending: Chip
    private lateinit var chipRejected: Chip
    private lateinit var chipCompleted: Chip

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_association_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val repository = CampaignRepositoryImpl(RetrofitClient.campaignApi)
        val getMyCampaignsUseCase = GetMyCampaignsUseCase(repository)
        val filterMyCampaignsUseCase = FilterMyCampaignsUseCase()
        val factory = AssociationDashboardViewModelFactory(getMyCampaignsUseCase, filterMyCampaignsUseCase)
        viewModel = ViewModelProvider(this, factory)[AssociationDashboardViewModel::class.java]

        // Initialize views
        rvCampaigns = view.findViewById(R.id.rvCampaigns)
        progressBar = view.findViewById(R.id.progressBar)
        llEmptyState = view.findViewById(R.id.llEmptyState)
        fabCreateCampaign = view.findViewById(R.id.fabCreateCampaign)

        chipAll = view.findViewById(R.id.chipAll)
        chipActive = view.findViewById(R.id.chipActive)
        chipPending = view.findViewById(R.id.chipPending)
        chipRejected = view.findViewById(R.id.chipRejected)
        chipCompleted = view.findViewById(R.id.chipCompleted)

        setupRecyclerView()
        setupFilterChips()
        setupFab()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        campaignAdapter = CampaignDashboardAdapter(
            onViewClick = { campaign ->
                // Navigate to campaign details
                navigateToCampaignDetails(campaign.id)
            },
            onEditClick = { campaign ->
                // Navigate to edit campaign
                navigateToCampaignForm(campaign)
            },
            onDeleteClick = { campaign ->
                // Show delete confirmation dialog
                showDeleteConfirmationDialog(campaign)
            }
        )
        rvCampaigns.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = campaignAdapter
        }
    }

    private fun setupFilterChips() {
        chipAll.setOnClickListener { viewModel.filterByStatus(CampaignStatus.ALL) }
        chipActive.setOnClickListener { viewModel.filterByStatus(CampaignStatus.ACTIVE) }
        chipPending.setOnClickListener { viewModel.filterByStatus(CampaignStatus.PENDING) }
        chipRejected.setOnClickListener { viewModel.filterByStatus(CampaignStatus.REJECTED) }
        chipCompleted.setOnClickListener { viewModel.filterByStatus(CampaignStatus.COMPLETED) }
    }

    private fun setupFab() {
        fabCreateCampaign.setOnClickListener {
            navigateToCampaignForm(null)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is CampaignListUiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        rvCampaigns.visibility = View.GONE
                        llEmptyState.visibility = View.GONE
                    }
                    is CampaignListUiState.Success -> {
                        progressBar.visibility = View.GONE
                        if (state.campaigns.isEmpty()) {
                            rvCampaigns.visibility = View.GONE
                            llEmptyState.visibility = View.VISIBLE
                        } else {
                            rvCampaigns.visibility = View.VISIBLE
                            llEmptyState.visibility = View.GONE
                            campaignAdapter.submitList(state.campaigns)
                        }
                    }
                    is CampaignListUiState.Error -> {
                        progressBar.visibility = View.GONE
                        rvCampaigns.visibility = View.GONE
                        llEmptyState.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    CampaignListUiState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun navigateToCampaignDetails(campaignId: Long) {
        // Navigate to CampaignDetailFragment with campaignId
        val fragment = com.example.hand4pal_android_mobile_app.features.campaign.presentation.CampaignDetailFragment()
        val bundle = Bundle().apply {
            putLong("campaignId", campaignId)
        }
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToCampaignForm(campaign: CampaignDTO?) {
        // Navigate to CampaignFormFragment
        val fragment = CampaignFormFragment()

        // Pass campaign data if editing
        if (campaign != null) {
            val bundle = Bundle().apply {
                putLong("campaignId", campaign.id)
                putString("title", campaign.title)
                putString("description", campaign.description)
                putDouble("targetAmount", campaign.targetAmount)
                putString("endDate", campaign.endDate)
                putString("category", campaign.category)
                putString("imageURL", campaign.imageURL)
            }
            fragment.arguments = bundle
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Show confirmation dialog before deleting a campaign
     */
    private fun showDeleteConfirmationDialog(campaign: CampaignDTO) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Campaign")
            .setMessage("Are you sure you want to delete \"${campaign.title}\"?\n\nThis action cannot be undone.")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteCampaign(campaign)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    /**
     * Delete campaign (to be implemented with backend API)
     */
    private fun deleteCampaign(campaign: CampaignDTO) {
        // TODO: Implement delete campaign API call
        Toast.makeText(
            requireContext(),
            "Delete campaign feature coming soon!\nCampaign: ${campaign.title}",
            Toast.LENGTH_LONG
        ).show()

        // For now, just refresh the list
        // In production, call deleteCampaignUseCase and refresh on success
        viewModel.refresh()
    }

    override fun onResume() {
        super.onResume()
        // Refresh campaigns when returning to this fragment
        viewModel.refresh()
    }
}

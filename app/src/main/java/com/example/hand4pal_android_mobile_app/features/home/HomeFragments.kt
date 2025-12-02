package com.example.hand4pal_android_mobile_app.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.data.CampaignRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignListUiState
import com.example.hand4pal_android_mobile_app.features.campaign.domain.GetCampaignsUseCase
import com.example.hand4pal_android_mobile_app.features.campaign.presentation.CampaignListAdapter
import com.example.hand4pal_android_mobile_app.features.campaign.presentation.CampaignListViewModel
import com.example.hand4pal_android_mobile_app.features.campaign.presentation.CampaignListViewModelFactory
import com.example.hand4pal_android_mobile_app.features.donation.presentation.DonationHistoryAdapter
import com.example.hand4pal_android_mobile_app.features.donation.presentation.DonationViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private val campaignViewModel: CampaignListViewModel by viewModels {
        CampaignListViewModelFactory(
            GetCampaignsUseCase(CampaignRepositoryImpl(RetrofitClient.campaignApi))
        )
    }

    private lateinit var campaignAdapter: CampaignListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCampaignsRecyclerView(view)
        setupClickListeners(view)
        observeCampaigns()

        // Load campaigns
        campaignViewModel.loadCampaigns()
    }

    private fun setupCampaignsRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvUrgentDonations)

        campaignAdapter = CampaignListAdapter { campaign ->
            // Navigate to campaign details using FragmentManager
            navigateToCampaignDetails(campaign.id)
        }

        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = campaignAdapter
    }

    private fun setupClickListeners(view: View) {
        // "See all" button in Urgent Donation section
        view.findViewById<TextView>(R.id.tvSeeAllUrgent)?.setOnClickListener {
            // Navigate to CampaignsFragment
            navigateToCampaignsFragment()
        }

        // Campaign category button
        view.findViewById<LinearLayout>(R.id.llCampaignCategory)?.setOnClickListener {
            navigateToCampaignsFragment()
        }
    }

    private fun navigateToCampaignDetails(campaignId: Long) {
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

    private fun navigateToCampaignsFragment() {
        val fragment = com.example.hand4pal_android_mobile_app.features.home.CampaignsFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun observeCampaigns() {
        viewLifecycleOwner.lifecycleScope.launch {
            campaignViewModel.uiState.collect { state ->
                when (state) {
                    is CampaignListUiState.Success -> {
                        // Show only first 5 campaigns for horizontal list
                        campaignAdapter.submitList(state.campaigns.take(5))
                    }
                    is CampaignListUiState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                    }
                    is CampaignListUiState.Loading -> {
                        // Optionally show loading indicator
                    }
                    CampaignListUiState.Idle -> {
                        // Do nothing
                    }
                }
            }
        }
    }
}

/**
 * CampaignsFragment - Main container for Campaign feature
 * This fragment hosts the CampaignListFragment using NavHostFragment
 * 
 * The actual campaign list and detail logic is in:
 * - com.example.hand4pal_android_mobile_app.features.campaign.presentation.CampaignListFragment
 * - com.example.hand4pal_android_mobile_app.features.campaign.presentation.CampaignDetailFragment
 */
class CampaignsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // The layout now contains a NavHostFragment for campaign navigation
        return inflater.inflate(R.layout.fragment_campaigns_host, container, false)
    }
}

class DonationFragment : Fragment() {

    // Injection du ViewModel
    private val viewModel: DonationViewModel by viewModels()
    private lateinit var adapter: DonationHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // ATTENTION : Utilisez le bon layout que vous avez créé (celui avec la liste)
        // Pas "fragment_settings", mais celui de l'historique
        return inflater.inflate(R.layout.fragment_donation_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Initialisation des vues
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerHistory)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar) // Assurez-vous que cet ID existe dans votre XML
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmpty) // Optionnel

        // 2. Setup Adapter
        adapter = DonationHistoryAdapter()
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        // 3. Observation des données
        viewModel.donations.observe(viewLifecycleOwner) { list ->
            progressBar?.visibility = View.GONE
            if (list.isEmpty()) {
                tvEmpty?.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                tvEmpty?.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                adapter.submitList(list)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            progressBar?.visibility = View.GONE
            if (errorMsg != null) {
                Toast.makeText(context, "Erreur: $errorMsg", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Chargement
        progressBar?.visibility = View.VISIBLE
        viewModel.loadDonations()
    }
}

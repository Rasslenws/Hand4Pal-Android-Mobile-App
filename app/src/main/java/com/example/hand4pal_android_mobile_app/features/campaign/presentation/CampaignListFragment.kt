package com.example.hand4pal_android_mobile_app.features.campaign.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.data.CampaignRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CampaignListUiState
import com.example.hand4pal_android_mobile_app.features.campaign.domain.GetCampaignsUseCase
import kotlinx.coroutines.launch

/**
 * Fragment for displaying list of campaigns
 * Follows the exact same pattern as ProfileFragment and LoginFragment
 * 
 * Responsibilities:
 * - Initialize ViewModel with dependencies
 * - Setup RecyclerView with adapter
 * - Collect StateFlow and update UI
 * - Handle navigation
 */
class CampaignListFragment : Fragment() {
    
    private val viewModel: CampaignListViewModel by viewModels {
        // Initialize Retrofit
        RetrofitClient.init(requireContext().applicationContext)
        
        // Create dependencies
        val repository = CampaignRepositoryImpl(RetrofitClient.campaignApi)
        val useCase = GetCampaignsUseCase(repository)
        
        // Create factory
        CampaignListViewModelFactory(useCase)
    }
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: CampaignListAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campaigns, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        setupRecyclerView()
        observeUiState()
        
        // Load campaigns
        viewModel.loadCampaigns()
    }
    
    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerViewCampaigns)
        progressBar = view.findViewById(R.id.progressBar)
    }
    
    private fun setupRecyclerView() {
        adapter = CampaignListAdapter { campaign ->
            // Navigate to detail screen
            val action = CampaignListFragmentDirections
                .actionCampaignsFragmentToCampaignDetailFragment(campaign.id)
            findNavController().navigate(action)
        }
        
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }
    
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is CampaignListUiState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                    is CampaignListUiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        recyclerView.visibility = View.GONE
                    }
                    is CampaignListUiState.Success -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
                        adapter.submitList(state.campaigns)
                        
                        if (state.campaigns.isEmpty()) {
                            Toast.makeText(
                                requireContext(),
                                "No active campaigns available",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    is CampaignListUiState.Error -> {
                        progressBar.visibility = View.GONE
                        recyclerView.visibility = View.VISIBLE
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
}

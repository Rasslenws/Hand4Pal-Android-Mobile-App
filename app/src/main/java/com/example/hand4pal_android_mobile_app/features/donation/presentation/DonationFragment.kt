package com.example.hand4pal_android_mobile_app.features.donation.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.donation.data.DonationRepositoryImpl

class DonationFragment : Fragment() {

    private val viewModel: DonationViewModel by viewModels { 
        DonationViewModelFactory(DonationRepositoryImpl())
    }
    private lateinit var adapter: DonationHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_donation_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerHistory)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        adapter = DonationHistoryAdapter()
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        viewModel.donations.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.loadDonations()
    }
}

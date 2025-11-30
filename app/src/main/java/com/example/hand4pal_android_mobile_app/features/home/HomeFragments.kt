package com.example.hand4pal_android_mobile_app.features.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.donation.presentation.DonationHistoryAdapter
import com.example.hand4pal_android_mobile_app.features.donation.presentation.DonationViewModel

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}

class CampaignsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campaigns, container, false)
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


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

class DonationFragment : Fragment() {

    // On utilise le ViewModel créé juste avant
    private val viewModel: DonationViewModel by viewModels()
    private lateinit var adapter: DonationHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Assurez-vous d'avoir le layout fragment_donation_history.xml (celui avec le titre et la liste)
        return inflater.inflate(R.layout.fragment_donation_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerHistory)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar) // ID du loading dans votre XML

        adapter = DonationHistoryAdapter()
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        // Observer les données
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

        // Charger les données
        viewModel.loadDonations()
    }
}

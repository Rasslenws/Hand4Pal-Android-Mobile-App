package com.example.hand4pal_android_mobile_app.features.dashboard.presentation

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.features.campaign.data.CampaignRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.campaign.domain.*
import com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel.CampaignFormViewModel
import com.example.hand4pal_android_mobile_app.features.dashboard.presentation.viewmodel.CampaignFormViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment for creating or updating a campaign
 * Handles both creation and update modes based on passed campaign
 */
class CampaignFormFragment : Fragment() {

    private lateinit var viewModel: CampaignFormViewModel
    private lateinit var tvFormTitle: TextView
    private lateinit var etTitle: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etTargetAmount: TextInputEditText
    private lateinit var actvCategory: AutoCompleteTextView
    private lateinit var etStartDate: TextInputEditText
    private lateinit var etEndDate: TextInputEditText
    private lateinit var etImageUrl: TextInputEditText
    private lateinit var btnSubmit: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tilStartDate: TextInputLayout

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private var startDate: String? = null
    private var endDate: String? = null

    // Categories
    private val categories = arrayOf(
        "HEALTH", "EDUCATION", "ENVIRONMENT", "POVERTY",
        "ANIMAL_WELFARE", "DISASTER_RELIEF", "OTHER"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_campaign_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        val repository = CampaignRepositoryImpl(RetrofitClient.campaignApi)
        val createCampaignUseCase = CreateCampaignUseCase(repository)
        val updateCampaignUseCase = UpdateCampaignUseCase(repository)
        val factory = CampaignFormViewModelFactory(createCampaignUseCase, updateCampaignUseCase)
        viewModel = ViewModelProvider(this, factory)[CampaignFormViewModel::class.java]

        // Initialize views
        tvFormTitle = view.findViewById(R.id.tvFormTitle)
        etTitle = view.findViewById(R.id.etTitle)
        etDescription = view.findViewById(R.id.etDescription)
        etTargetAmount = view.findViewById(R.id.etTargetAmount)
        actvCategory = view.findViewById(R.id.actvCategory)
        etStartDate = view.findViewById(R.id.etStartDate)
        etEndDate = view.findViewById(R.id.etEndDate)
        etImageUrl = view.findViewById(R.id.etImageUrl)
        btnSubmit = view.findViewById(R.id.btnSubmit)
        progressBar = view.findViewById(R.id.progressBar)
        tilStartDate = view.findViewById(R.id.tilStartDate)

        setupCategoryDropdown()
        setupDatePickers()
        setupSubmitButton()
        observeViewModel()
        checkForExistingCampaign()
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categories)
        actvCategory.setAdapter(adapter)
    }

    private fun setupDatePickers() {
        etStartDate.setOnClickListener {
            showDatePicker { date ->
                startDate = date
                etStartDate.setText(date)
            }
        }

        etEndDate.setOnClickListener {
            showDatePicker { date ->
                endDate = date
                etEndDate.setText(date)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(dateFormatter.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupSubmitButton() {
        btnSubmit.setOnClickListener {
            if (validateInputs()) {
                if (viewModel.isUpdateMode()) {
                    updateCampaign()
                } else {
                    createCampaign()
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val targetAmountStr = etTargetAmount.text.toString().trim()
        val category = actvCategory.text.toString().trim()

        if (title.isEmpty()) {
            etTitle.error = "Title is required"
            return false
        }
        if (description.isEmpty()) {
            etDescription.error = "Description is required"
            return false
        }
        if (targetAmountStr.isEmpty()) {
            etTargetAmount.error = "Target amount is required"
            return false
        }
        if (category.isEmpty()) {
            actvCategory.error = "Category is required"
            return false
        }
        if (!viewModel.isUpdateMode() && startDate == null) {
            Toast.makeText(requireContext(), "Start date is required", Toast.LENGTH_SHORT).show()
            return false
        }
        if (endDate == null) {
            Toast.makeText(requireContext(), "End date is required", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun createCampaign() {
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val targetAmount = etTargetAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
        val category = actvCategory.text.toString().trim()
        // Filter out empty strings and literal "null" string
        val imageUrlText = etImageUrl.text.toString().trim()
        val imageUrl = if (imageUrlText.isEmpty() || imageUrlText.equals("null", ignoreCase = true)) {
            null
        } else {
            imageUrlText
        }

        // Backend expects LocalDateTime format: yyyy-MM-ddTHH:mm:ss
        viewModel.createCampaign(
            title = title,
            description = description,
            targetAmount = targetAmount,
            startDate = startDate?.toLocalDateTimeString(), // Convert to yyyy-MM-ddTHH:mm:ss
            endDate = endDate?.toLocalDateTimeString(),     // Convert to yyyy-MM-ddTHH:mm:ss
            category = category,
            imageURL = imageUrl
        )
    }

    /**
     * Show custom dialog after campaign creation
     * Informs user about pending status and admin approval
     */
    private fun showCampaignCreatedDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("✅ Campaign Created Successfully!")
        builder.setMessage(
            "Your campaign has been created and submitted for review.\n\n" +
            "📋 Status: PENDING\n" +
            "⏳ Waiting for administrator approval\n\n" +
            "You will be notified once your campaign is approved and becomes visible to donors."
        )
        builder.setPositiveButton("Got it!") { dialog, _ ->
            dialog.dismiss()
            parentFragmentManager.popBackStack()
        }
        builder.setCancelable(false)
        builder.show()
    }

    /**
     * Converts date from yyyy-MM-dd to yyyy-MM-ddT00:00:00 format for backend LocalDateTime
     * If date already contains time (T), returns it as-is
     */
    private fun String.toLocalDateTimeString(): String {
        return if (this.contains("T")) {
            // Date already has time component, return as-is
            this
        } else {
            // Date only, add time component
            "${this}T00:00:00"
        }
    }

    private fun updateCampaign() {
        val campaign = viewModel.existingCampaign.value ?: return
        val title = etTitle.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val targetAmount = etTargetAmount.text.toString().trim().toDoubleOrNull() ?: 0.0
        val category = actvCategory.text.toString().trim()
        // Filter out empty strings and literal "null" string
        val imageUrlText = etImageUrl.text.toString().trim()
        val imageUrl = if (imageUrlText.isEmpty() || imageUrlText.equals("null", ignoreCase = true)) {
            null
        } else {
            imageUrlText
        }

        // Backend expects LocalDateTime format: yyyy-MM-ddTHH:mm:ss
        viewModel.updateCampaign(
            campaignId = campaign.id,
            title = title,
            description = description,
            targetAmount = targetAmount,
            endDate = endDate?.toLocalDateTimeString(), // Convert to yyyy-MM-ddTHH:mm:ss
            category = category,
            imageURL = imageUrl
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is CampaignFormUiState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnSubmit.isEnabled = false
                    }
                    is CampaignFormUiState.Success -> {
                        progressBar.visibility = View.GONE
                        btnSubmit.isEnabled = true

                        if (viewModel.isUpdateMode()) {
                            // Show simple toast for update
                            Toast.makeText(requireContext(), "Campaign updated successfully", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.popBackStack()
                        } else {
                            // Show custom dialog for creation
                            showCampaignCreatedDialog()
                        }
                    }
                    is CampaignFormUiState.Error -> {
                        progressBar.visibility = View.GONE
                        btnSubmit.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    CampaignFormUiState.Idle -> {
                        progressBar.visibility = View.GONE
                        btnSubmit.isEnabled = true
                    }
                }
            }
        }
    }

    private fun checkForExistingCampaign() {
        // Get campaign data from arguments
        val args = arguments

        if (args != null && args.containsKey("campaignId")) {
            // Update mode - reconstruct campaign from arguments
            val campaignId = args.getLong("campaignId")
            val title = args.getString("title", "")
            val description = args.getString("description", "")
            val targetAmount = args.getDouble("targetAmount", 0.0)
            val endDateStr = args.getString("endDate")
            val category = args.getString("category", "")
            val imageURL = args.getString("imageURL")

            // Create a CampaignDTO for the ViewModel
            val campaign = CampaignDTO(
                id = campaignId,
                title = title,
                description = description,
                category = category,
                status = "ACTIVE", // Default, not used in form
                targetAmount = targetAmount,
                collectedAmount = 0.0, // Not needed for form
                associationId = 0, // Not needed for form
                createdAt = null,
                updatedAt = null,
                endDate = endDateStr,
                imageURL = imageURL,
                comments = emptyList(),
                donations = emptyList()
            )

            // Update mode
            viewModel.setExistingCampaign(campaign)
            tvFormTitle.text = getString(R.string.update_campaign_title)
            btnSubmit.text = getString(R.string.update_campaign_button)

            // Pre-fill form
            etTitle.setText(campaign.title)
            etDescription.setText(campaign.description)
            etTargetAmount.setText(campaign.targetAmount.toString())
            actvCategory.setText(campaign.category, false)
            campaign.endDate?.let {
                endDate = it
                etEndDate.setText(it)
            }
            campaign.imageURL?.let { etImageUrl.setText(it) }

            // Hide start date in update mode
            tilStartDate.visibility = View.GONE
        } else {
            // Create mode
            tvFormTitle.text = getString(R.string.create_campaign_title)
            btnSubmit.text = getString(R.string.create_campaign_button)
            tilStartDate.visibility = View.VISIBLE
        }
    }
}


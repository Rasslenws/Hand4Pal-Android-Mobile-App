package com.example.hand4pal_android_mobile_app.features.auth.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.auth.domain.AuthState
import com.example.hand4pal_android_mobile_app.features.auth.domain.RegisterAssociationRequest
import com.example.hand4pal_android_mobile_app.features.auth.domain.RegisterCitizenRequest
import com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel.AuthViewModel
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels {
        (requireActivity() as com.example.hand4pal_android_mobile_app.features.auth.presentation.AuthActivity).authViewModelFactory
    }

    private lateinit var roleGroup: RadioGroup
    private lateinit var citizenRadio: RadioButton
    private lateinit var associationRadio: RadioButton
    
    // Citizen fields
    private lateinit var citizenContainer: LinearLayout
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    
    // Association fields
    private lateinit var associationContainer: LinearLayout
    private lateinit var etAssocName: TextInputEditText
    private lateinit var etOwnerFirstName: TextInputEditText
    private lateinit var etOwnerLastName: TextInputEditText
    private lateinit var etAssocEmail: TextInputEditText
    private lateinit var etAssocPhone: TextInputEditText
    private lateinit var etAssocPassword: TextInputEditText
    private lateinit var etAssocConfirmPassword: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var etWebsite: TextInputEditText
    
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        roleGroup = view.findViewById(R.id.roleGroup)
        citizenRadio = view.findViewById(R.id.citizenRadio)
        associationRadio = view.findViewById(R.id.associationRadio)
        
        citizenContainer = view.findViewById(R.id.citizenContainer)
        etFirstName = view.findViewById(R.id.etFirstName)
        etLastName = view.findViewById(R.id.etLastName)
        etEmail = view.findViewById(R.id.etEmail)
        etPhone = view.findViewById(R.id.etPhone)
        etPassword = view.findViewById(R.id.etPassword)
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword)
        
        associationContainer = view.findViewById(R.id.associationContainer)
        etAssocName = view.findViewById(R.id.etAssocName)
        etOwnerFirstName = view.findViewById(R.id.etOwnerFirstName)
        etOwnerLastName = view.findViewById(R.id.etOwnerLastName)
        etAssocEmail = view.findViewById(R.id.etAssocEmail)
        etAssocPhone = view.findViewById(R.id.etAssocPhone)
        etAssocPassword = view.findViewById(R.id.etAssocPassword)
        etAssocConfirmPassword = view.findViewById(R.id.etAssocConfirmPassword)
        etDescription = view.findViewById(R.id.etDescription)
        etAddress = view.findViewById(R.id.etAddress)
        etWebsite = view.findViewById(R.id.etWebsite)
        
        btnRegister = view.findViewById(R.id.btnRegister)
        tvLogin = view.findViewById(R.id.tvLogin)
        progressBar = view.findViewById(R.id.progressBar)

        setupRoleSelection()
        setupClickListeners()
        observeAuthState()
    }

    private fun setupRoleSelection() {
        roleGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.citizenRadio -> {
                    citizenContainer.visibility = View.VISIBLE
                    associationContainer.visibility = View.GONE
                    clearAllErrors()
                }
                R.id.associationRadio -> {
                    citizenContainer.visibility = View.GONE
                    associationContainer.visibility = View.VISIBLE
                    clearAllErrors()
                }
            }
        }
        
        // Default to citizen
        citizenRadio.isChecked = true
    }

    private fun clearAllErrors() {
        // Clear citizen errors
        etFirstName.error = null
        etLastName.error = null
        etEmail.error = null
        etPhone.error = null
        etPassword.error = null
        etConfirmPassword.error = null
        
        // Clear association errors
        etAssocName.error = null
        etOwnerFirstName.error = null
        etOwnerLastName.error = null
        etAssocEmail.error = null
        etAssocPhone.error = null
        etAssocPassword.error = null
        etAssocConfirmPassword.error = null
    }

    private fun setupClickListeners() {
        btnRegister.setOnClickListener {
            when {
                citizenRadio.isChecked -> registerCitizen()
                associationRadio.isChecked -> registerAssociation()
            }
        }

        tvLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun registerCitizen() {
        val firstName = etFirstName.text.toString().trim()
        val lastName = etLastName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val password = etPassword.text.toString()
        val confirmPassword = etConfirmPassword.text.toString()

        if (!validateCitizenInputs(firstName, lastName, email, phone, password, confirmPassword)) {
            return
        }

        val request = RegisterCitizenRequest(
            firstName = firstName,
            lastName = lastName,
            email = email,
            phone = phone,
            password = password
        )

        viewModel.registerCitizen(request)
    }

    private fun registerAssociation() {
        val assocName = etAssocName.text.toString().trim()
        val ownerFirstName = etOwnerFirstName.text.toString().trim()
        val ownerLastName = etOwnerLastName.text.toString().trim()
        val email = etAssocEmail.text.toString().trim()
        val phone = etAssocPhone.text.toString().trim()
        val password = etAssocPassword.text.toString()
        val confirmPassword = etAssocConfirmPassword.text.toString()
        val description = etDescription.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val website = etWebsite.text.toString().trim()

        if (!validateAssociationInputs(assocName, ownerFirstName, ownerLastName, email, phone, password, confirmPassword)) {
            return
        }

        val request = RegisterAssociationRequest(
            associationName = assocName,
            ownerFirstName = ownerFirstName,
            ownerLastName = ownerLastName,
            email = email,
            phone = phone,
            password = password,
            description = description.ifEmpty { null },
            address = address.ifEmpty { null },
            webSite = website.ifEmpty { null }
        )

        viewModel.registerAssociation(request)
    }

    private fun validateCitizenInputs(
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (firstName.isEmpty()) {
            etFirstName.error = "First name is required"
            return false
        }
        if (lastName.isEmpty()) {
            etLastName.error = "Last name is required"
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Valid email is required"
            return false
        }
        if (phone.isEmpty()) {
            etPhone.error = "Phone is required"
            return false
        }
        if (password.length < 6) {
            etPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun validateAssociationInputs(
        assocName: String,
        ownerFirstName: String,
        ownerLastName: String,
        email: String,
        phone: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (assocName.isEmpty()) {
            etAssocName.error = "Association name is required"
            return false
        }
        if (ownerFirstName.isEmpty()) {
            etOwnerFirstName.error = "Owner first name is required"
            return false
        }
        if (ownerLastName.isEmpty()) {
            etOwnerLastName.error = "Owner last name is required"
            return false
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etAssocEmail.error = "Valid email is required"
            return false
        }
        if (phone.isEmpty()) {
            etAssocPhone.error = "Phone is required"
            return false
        }
        if (password.length < 6) {
            etAssocPassword.error = "Password must be at least 6 characters"
            return false
        }
        if (password != confirmPassword) {
            etAssocConfirmPassword.error = "Passwords do not match"
            return false
        }
        return true
    }

    private fun observeAuthState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {
                    is AuthState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        btnRegister.isEnabled = false
                    }
                    is AuthState.Success -> {
                        progressBar.visibility = View.GONE
                        btnRegister.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        // Reset state before navigating back
                        viewModel.resetState()
                        // Go back to login
                        parentFragmentManager.popBackStack()
                    }
                    is AuthState.Error -> {
                        progressBar.visibility = View.GONE
                        btnRegister.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    AuthState.Idle -> {
                        progressBar.visibility = View.GONE
                        btnRegister.isEnabled = true
                    }
                }
            }
        }
    }
}

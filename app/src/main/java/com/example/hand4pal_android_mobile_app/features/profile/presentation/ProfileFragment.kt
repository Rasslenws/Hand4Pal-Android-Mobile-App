package com.example.hand4pal_android_mobile_app.features.profile.presentation

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.datastore.preferences.core.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.DataStoreKeys
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.core.network.dataStore
import com.example.hand4pal_android_mobile_app.features.auth.presentation.AuthActivity
import com.example.hand4pal_android_mobile_app.features.profile.data.ProfileRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.profile.domain.*
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private val viewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(
            ProfileRepositoryImpl(RetrofitClient.profileApi)
        )
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var profileContainer: LinearLayout
    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvUserRole: TextView
    private lateinit var ivAvatar: ImageView
    private lateinit var btnEditProfile: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnLogout: Button

    private var currentProfile: ProfileResponse? = null

    // Avatar drawables
    private val avatarDrawables = listOf(
        R.drawable.avatar1,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        profileContainer = view.findViewById(R.id.profileContainer)
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvUserRole = view.findViewById(R.id.tvUserRole)
        ivAvatar = view.findViewById(R.id.ivAvatar)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnChangePassword = view.findViewById(R.id.btnChangePassword)
        btnLogout = view.findViewById(R.id.btnLogout)

        setupClickListeners()
        observeProfileState()
        
        viewModel.loadProfile()
    }

    private fun setupClickListeners() {
        btnEditProfile.setOnClickListener {
            showEditProfileDialog()
        }

        btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        btnLogout.setOnClickListener {
            logout()
        }
        
        ivAvatar.setOnClickListener {
            showAvatarSelectionDialog()
        }
    }

    private fun observeProfileState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.profileState.collect { state ->
                when (state) {
                    is ProfileState.Loading -> {
                        progressBar.visibility = View.VISIBLE
                        profileContainer.visibility = View.GONE
                    }
                    is ProfileState.Success -> {
                        progressBar.visibility = View.GONE
                        profileContainer.visibility = View.VISIBLE
                        currentProfile = state.profile
                        updateUI(state.profile)
                    }
                    is ProfileState.Error -> {
                        progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    ProfileState.Idle -> {
                        progressBar.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.updateState.collect { state ->
                when (state) {
                    is ProfileState.Success -> {
                        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        viewModel.resetUpdateState()
                    }
                    is ProfileState.Error -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                        viewModel.resetUpdateState()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun updateUI(profile: ProfileResponse) {
        tvUserName.text = "${profile.firstName} ${profile.lastName}"
        tvUserEmail.text = profile.email
        tvUserRole.text = profile.role
        
        // Load avatar based on profilePictureUrl
        profile.profilePictureUrl?.let { avatarUrl ->
            val avatarIndex = avatarUrl.replace("avatar", "").toIntOrNull()?.minus(1)
            if (avatarIndex != null && avatarIndex in avatarDrawables.indices) {
                ivAvatar.setImageResource(avatarDrawables[avatarIndex])
            }
        }
    }

    private fun showEditProfileDialog() {
        val profile = currentProfile ?: return

        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_profile, null)
        val etFirstName = dialogView.findViewById<TextInputEditText>(R.id.etFirstName)
        val etLastName = dialogView.findViewById<TextInputEditText>(R.id.etLastName)
        val etEmail = dialogView.findViewById<TextInputEditText>(R.id.etEmail)
        val etPhone = dialogView.findViewById<TextInputEditText>(R.id.etPhone)

        etFirstName.setText(profile.firstName)
        etLastName.setText(profile.lastName)
        etEmail.setText(profile.email)
        etPhone.setText(profile.phone)

        AlertDialog.Builder(requireContext())
            .setTitle("Edit Profile")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val request = UpdateProfileRequest(
                    firstName = etFirstName.text.toString(),
                    lastName = etLastName.text.toString(),
                    email = etEmail.text.toString(),
                    phone = etPhone.text.toString(),
                    profilePictureUrl = profile.profilePictureUrl
                )
                viewModel.updateProfile(request)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<TextInputEditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<TextInputEditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<TextInputEditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(requireContext())
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val current = etCurrentPassword.text.toString()
                val new = etNewPassword.text.toString()
                val confirm = etConfirmPassword.text.toString()

                if (new != confirm) {
                    Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val request = ChangePasswordRequest(
                    currentPassword = current,
                    newPassword = new,
                    confirmPassword = confirm
                )
                viewModel.changePassword(request)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAvatarSelectionDialog() {
        val avatarNames = arrayOf("Avatar 1", "Avatar 2", "Avatar 3", "Avatar 4")
        
        AlertDialog.Builder(requireContext())
            .setTitle("Select Avatar")
            .setItems(avatarNames) { _, which ->
                val avatarUrl = "avatar${which + 1}"
                val request = UpdateProfileRequest(
                    firstName = currentProfile?.firstName,
                    lastName = currentProfile?.lastName,
                    email = currentProfile?.email,
                    phone = currentProfile?.phone,
                    profilePictureUrl = avatarUrl
                )
                viewModel.updateProfile(request)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun logout() {
        viewLifecycleOwner.lifecycleScope.launch {
            requireContext().dataStore.edit { preferences ->
                preferences.remove(DataStoreKeys.TOKEN_KEY)
            }
            
            val intent = Intent(requireContext(), AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}

package com.example.hand4pal_android_mobile_app.features.auth.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.auth.domain.AuthState
import com.example.hand4pal_android_mobile_app.features.auth.domain.LoginRequest
import com.example.hand4pal_android_mobile_app.features.auth.presentation.AuthActivity
import com.example.hand4pal_android_mobile_app.features.auth.presentation.register.RegisterFragment
import com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private val viewModel: AuthViewModel by activityViewModels {
        (requireActivity() as AuthActivity).authViewModelFactory
    }

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvRegister: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnLogin = view.findViewById(R.id.btnLogin)
        tvRegister = view.findViewById(R.id.tvRegister)
        progressBar = view.findViewById(R.id.progressBar)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (validateInputs(email, password)) {
                viewModel.login(LoginRequest(email, password))
            }
        }

        tvRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.authFragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        observeAuthState()
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Valid email is required"
            return false
        }
        if (password.isEmpty()) {
            etPassword.error = "Password is required"
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
                        btnLogin.isEnabled = false
                    }
                    is AuthState.Success -> {
                        progressBar.visibility = View.GONE
                        btnLogin.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    is AuthState.Error -> {
                        progressBar.visibility = View.GONE
                        btnLogin.isEnabled = true
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                    AuthState.Idle -> {
                        progressBar.visibility = View.GONE
                        btnLogin.isEnabled = true
                    }
                }
            }
        }

        // Observe login response to navigate
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginResponse.collect { response ->
                if (response != null) {
                    // Save token
                    (requireActivity() as AuthActivity).saveToken(response.token)
                    // Navigate to main
                    (requireActivity() as AuthActivity).navigateToMain()
                }
            }
        }
    }
}

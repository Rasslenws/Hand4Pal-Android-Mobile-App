package com.example.hand4pal_android_mobile_app.features.auth.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hand4pal_android_mobile_app.MainActivity
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.databinding.FragmentLoginBinding
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthLocalDataSource
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthRemoteDataSource
import com.example.hand4pal_android_mobile_app.features.auth.data.repository.AuthRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.auth.presentation.register.RegisterFragment
import com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel.AuthViewModelFactory

class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        // 1. Initialize Dependencies (DataSource Architecture)
        val context = requireContext()

        // A. Local Data Source (DataStore/SharedPref)
        val localDataSource = AuthLocalDataSource(context)

        // B. Remote Data Source (API)
        // Note: getAuthApi now requires localDataSource for the interceptor
        val api = RetrofitClient.getAuthApi(context, localDataSource)
        val remoteDataSource = AuthRemoteDataSource(api)

        // C. Repository (Combines Local + Remote)
        val repository = AuthRepositoryImpl(remoteDataSource, localDataSource)

        // D. ViewModel
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        // 2. Navigation to Register
        binding.tvGoToRegister.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment())
                .addToBackStack(null)
                .commit()
        }

        // 3. Login Button Click
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                setLoading(true)
                viewModel.login(email, password)
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Observe Login State
        viewModel.loginState.observe(viewLifecycleOwner) { result ->
            setLoading(false)

            result.onSuccess { authResponse ->
                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()

                // Navigate to MainActivity
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

            result.onFailure { error ->
                Toast.makeText(context, "Login Failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Helper to toggle button state during loading
    private fun setLoading(isLoading: Boolean) {
        binding.btnLogin.isEnabled = !isLoading
        binding.btnLogin.text = if (isLoading) "Logging in..." else "LOGIN"
        binding.btnLogin.alpha = if (isLoading) 0.5f else 1.0f
    }
}

package com.example.hand4pal_android_mobile_app.features.auth.presentation.register

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.core.network.RetrofitClient
import com.example.hand4pal_android_mobile_app.databinding.FragmentRegisterBinding
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthLocalDataSource
import com.example.hand4pal_android_mobile_app.features.auth.data.datasource.AuthRemoteDataSource
import com.example.hand4pal_android_mobile_app.features.auth.data.repository.AuthRepositoryImpl
import com.example.hand4pal_android_mobile_app.features.auth.presentation.dialog.SuccessDialogFragment
import com.example.hand4pal_android_mobile_app.features.auth.presentation.login.LoginFragment
import com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel.AuthViewModel
import com.example.hand4pal_android_mobile_app.features.auth.presentation.viewmodel.AuthViewModelFactory

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        // 1. Initialize Dependencies (DataSource Architecture)
        val context = requireContext()

        // A. Local Data Source
        val localDataSource = AuthLocalDataSource(context)

        // B. Remote Data Source (API)
        val api = RetrofitClient.getAuthApi(context, localDataSource)
        val remoteDataSource = AuthRemoteDataSource(api)

        // C. Repository
        val repository = AuthRepositoryImpl(remoteDataSource, localDataSource)

        // D. ViewModel
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        // 2. Navigation vers Login
        binding.tvGoToLogin.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LoginFragment())
                .commit()
        }

        // 3. Action du bouton Créer Compte
        binding.btnRegister.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            // Attention aux IDs XML
            val email = binding.etRegEmail.text.toString().trim()
            val password = binding.etRegPassword.text.toString().trim()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                setLoading(true)
                viewModel.register(firstName, lastName, email, password)
            } else {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // 4. Observer la réponse du Backend
        viewModel.registerState.observe(viewLifecycleOwner) { result ->
            setLoading(false)

            result.onSuccess { user ->
                // Affiche la popup de succès
                val dialog = SuccessDialogFragment()
                dialog.show(parentFragmentManager, "SuccessDialog")

                // Redirection automatique vers le Login après 2 secondes
                view.postDelayed({
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, LoginFragment())
                        .commit()
                }, 2000)
            }

            result.onFailure { error ->
                Toast.makeText(context, "Registration Failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnRegister.isEnabled = !isLoading
        binding.btnRegister.text = if (isLoading) "Creating Account..." else "Create Account"
        binding.btnRegister.alpha = if (isLoading) 0.5f else 1.0f
    }
}

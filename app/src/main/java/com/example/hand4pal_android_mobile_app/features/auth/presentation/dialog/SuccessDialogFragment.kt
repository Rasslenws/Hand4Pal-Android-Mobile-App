package com.example.hand4pal_android_mobile_app.features.auth.presentation.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.hand4pal_android_mobile_app.databinding.DialogSuccessBinding
import android.content.Intent
import com.example.hand4pal_android_mobile_app.MainActivity

class SuccessDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogSuccessBinding.inflate(LayoutInflater.from(context))

        binding.btnOk.setOnClickListener {
            dismiss()
            // Redirige vers la MainActivity (Dashboard) après succès
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Ferme AuthActivity
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        // Rend le fond de la popup transparent pour voir les coins arrondis
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return dialog
    }
}

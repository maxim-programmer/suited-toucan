package com.example.drivenext.presentation.auth.register

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentRegistrationSuccessBinding

class RegistrationSuccessFragment : Fragment(R.layout.fragment_registration_success) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRegistrationSuccessBinding.bind(view)
        binding.nextButton.setOnClickListener {
            findNavController().navigate(R.id.action_success_to_main)
        }
    }
}

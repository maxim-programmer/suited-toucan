package com.example.drivenext.presentation.auth.welcome

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment(R.layout.fragment_welcome) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentWelcomeBinding.bind(view)
        val navController = findNavController()

        binding.loginButton.setOnClickListener {
            navController.navigate(R.id.action_welcome_to_login)
        }
        binding.registerButton.setOnClickListener {
            navController.navigate(R.id.action_welcome_to_registration)
        }
    }
}

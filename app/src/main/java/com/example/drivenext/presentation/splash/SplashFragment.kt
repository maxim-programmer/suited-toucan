package com.example.drivenext.presentation.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.domain.model.StartDestination
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels { appContainer.viewModelFactory }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectWhenStarted(viewModel.events) { destination ->
            val action = when (destination) {
                StartDestination.NoConnection -> R.id.action_splash_to_noConnection
                StartDestination.Onboarding -> R.id.action_splash_to_onboarding
                StartDestination.Auth -> R.id.action_splash_to_auth
                StartDestination.Main -> R.id.action_splash_to_main
            }
            findNavController().navigate(action)
        }
    }
}

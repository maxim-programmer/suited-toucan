package com.example.drivenext.presentation.noconnection

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentNoConnectionBinding
import com.example.drivenext.domain.model.StartDestination
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class NoConnectionFragment : Fragment(R.layout.fragment_no_connection) {

    private val viewModel: NoConnectionViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentNoConnectionBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNoConnectionBinding.bind(view)

        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }

        collectWhenStarted(viewModel.isChecking) { isChecking ->
            binding.retryButton.isEnabled = !isChecking
            binding.retryButton.setText(
                if (isChecking) R.string.no_connection_checking else R.string.no_connection_retry
            )
        }

        collectWhenStarted(viewModel.events) { destination ->
            val action = when (destination) {
                StartDestination.Onboarding -> R.id.action_noConnection_to_onboarding
                StartDestination.Auth -> R.id.action_noConnection_to_auth
                StartDestination.NoConnection -> return@collectWhenStarted
            }
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

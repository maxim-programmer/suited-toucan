package com.example.drivenext.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentSettingsBinding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.navigateToTab
import com.example.drivenext.presentation.common.showErrorDialog
import com.example.drivenext.presentation.common.showInfoDialog

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSettingsBinding.bind(view)
        val navController = findNavController()

        binding.profileContainer.setOnClickListener { navController.navigate(R.id.profileFragment) }
        binding.bookingsRow.setOnClickListener { navController.navigateToTab(R.id.bookingsFragment) }
        binding.themeRow.setOnClickListener {
            showInfoDialog(R.string.settings_theme, R.string.feature_in_development)
        }
        binding.notificationsRow.setOnClickListener {
            showInfoDialog(R.string.settings_notifications, R.string.feature_in_development)
        }
        binding.connectCarRow.setOnClickListener { navController.navigate(R.id.addCarFragment) }
        binding.helpRow.setOnClickListener {
            showInfoDialog(R.string.settings_help, R.string.feature_in_development)
        }
        binding.inviteRow.setOnClickListener { shareInvitation() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }

        collectWhenStarted(viewModel.state) { state ->
            binding.progressIndicator.isVisible = state.isLoading
            binding.errorContainer.isVisible = state.hasError
            val profile = state.profile
            binding.profileContainer.isVisible = profile != null
            if (profile != null) {
                binding.nameTextView.text =
                    profile.fullName.ifBlank { getString(R.string.profile_default_name) }
                binding.emailTextView.text = profile.email
                binding.avatarImageView.load(profile.avatarUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_avatar_placeholder)
                    error(R.drawable.ic_avatar_placeholder)
                    fallback(R.drawable.ic_avatar_placeholder)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun shareInvitation() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, getString(R.string.settings_invite_text))
        }
        try {
            startActivity(Intent.createChooser(intent, getString(R.string.settings_invite_chooser)))
        } catch (e: ActivityNotFoundException) {
            showErrorDialog(getString(R.string.error_share_unavailable))
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

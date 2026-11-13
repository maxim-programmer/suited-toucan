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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentSettingsBinding
import com.example.drivenext.domain.model.ThemeMode
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.applyThemeMode
import com.example.drivenext.presentation.common.collectWhenStarted
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
        binding.bookingsRow.setOnClickListener { navController.navigate(R.id.bookingsFragment) }
        binding.themeRow.setOnClickListener { showThemeDialog() }
        binding.notificationsRow.setOnClickListener {
            showInfoDialog(R.string.settings_notifications, R.string.feature_in_development)
        }
        binding.connectCarRow.setOnClickListener { navController.navigate(R.id.add_car_graph) }
        binding.myCarsRow.setOnClickListener { navController.navigate(R.id.myCarsFragment) }
        binding.helpRow.setOnClickListener { navController.navigate(R.id.helpFragment) }
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

    private fun showThemeDialog() {
        val modes = ThemeMode.entries
        val labels = modes.map { mode ->
            getString(
                when (mode) {
                    ThemeMode.System -> R.string.theme_system
                    ThemeMode.Light -> R.string.theme_light
                    ThemeMode.Dark -> R.string.theme_dark
                }
            )
        }.toTypedArray()
        val checked = modes.indexOf(viewModel.state.value.themeMode)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.settings_theme)
            .setSingleChoiceItems(labels, checked) { dialog, which ->
                val mode = modes[which]
                dialog.dismiss()
                viewModel.onThemeSelected(mode)
                applyThemeMode(mode)
            }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
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
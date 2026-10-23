package com.example.drivenext.presentation.profile

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.load
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentProfileBinding
import com.example.drivenext.domain.model.Gender
import com.example.drivenext.domain.model.UserProfile
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.createCameraOutputUri
import com.example.drivenext.presentation.common.showErrorDialog
import com.example.drivenext.presentation.common.showInfoDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) viewModel.onAvatarSelected(uri.toString())
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val uri = viewModel.pendingCameraUri
        if (success && uri != null) viewModel.onAvatarSelected(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProfileBinding.bind(view)

        binding.avatarContainer.setOnClickListener { showPhotoSourceDialog() }
        binding.changePasswordTextView.setOnClickListener { viewModel.onChangePasswordClicked() }
        binding.logoutTextView.setOnClickListener { viewModel.onLogoutClicked() }
        binding.errorLogoutButton.setOnClickListener { viewModel.onLogoutClicked() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }

        collectWhenStarted(viewModel.state) { state ->
            binding.progressIndicator.isVisible = state.isLoading
            binding.errorContainer.isVisible = state.hasError
            binding.contentScrollView.isVisible = state.profile != null
            state.profile?.let { render(it) }
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                ProfileEvent.LoggedOut -> findNavController().navigate(R.id.action_global_welcome)
                is ProfileEvent.ShowError -> showErrorDialog(event.message)
                is ProfileEvent.ShowInfo -> showInfoDialog(event.titleRes, event.messageRes)
            }
        }
    }

    private fun render(profile: UserProfile) {
        binding.nameTextView.text = profile.fullName.ifBlank { getString(R.string.profile_default_name) }
        binding.emailValueTextView.text = profile.email

        val joinedAt = profile.joinedAt
        binding.joinedTextView.isVisible = joinedAt != null
        if (joinedAt != null) {
            val months = resources.getStringArray(R.array.profile_months_prepositional)
            binding.joinedTextView.text =
                getString(R.string.profile_joined, "${months[joinedAt.monthValue - 1]} ${joinedAt.year}")
        }

        binding.genderValueTextView.text = when (profile.gender) {
            Gender.Male -> getString(R.string.register_gender_male)
            Gender.Female -> getString(R.string.register_gender_female)
            null -> getString(R.string.profile_value_empty)
        }

        val googleEmail = profile.googleEmail
        binding.googleContainer.isVisible = googleEmail != null
        binding.googleValueTextView.text = googleEmail.orEmpty()

        binding.avatarImageView.load(profile.avatarUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_avatar_placeholder)
            error(R.drawable.ic_avatar_placeholder)
            fallback(R.drawable.ic_avatar_placeholder)
        }
    }

    private fun showPhotoSourceDialog() {
        if (viewModel.state.value.isLoading) return
        val options = arrayOf(
            getString(R.string.photo_source_gallery),
            getString(R.string.photo_source_camera)
        )
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.photo_source_title)
            .setItems(options) { _, which ->
                if (which == 0) openGallery() else openCamera()
            }
            .show()
    }

    private fun openGallery() {
        try {
            pickImage.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } catch (e: ActivityNotFoundException) {
            showErrorDialog(getString(R.string.error_gallery_unavailable))
        }
    }

    private fun openCamera() {
        try {
            val outputUri = requireContext().createCameraOutputUri()
            viewModel.pendingCameraUri = outputUri.toString()
            takePhoto.launch(outputUri)
        } catch (e: ActivityNotFoundException) {
            showErrorDialog(getString(R.string.error_camera_unavailable))
        } catch (e: IllegalArgumentException) {
            showErrorDialog(getString(R.string.error_camera_unavailable))
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

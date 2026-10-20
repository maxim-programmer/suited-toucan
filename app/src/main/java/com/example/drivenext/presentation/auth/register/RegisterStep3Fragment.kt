package com.example.drivenext.presentation.auth.register

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentRegisterStep3Binding
import com.example.drivenext.domain.validation.DateFormats
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.createCameraOutputUri
import com.example.drivenext.presentation.common.showDatePicker
import com.example.drivenext.presentation.common.showErrorDialog
import com.example.drivenext.presentation.common.showImageFromUri
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RegisterStep3Fragment : Fragment(R.layout.fragment_register_step3) {

    private val viewModel: RegistrationViewModel by navGraphViewModels(R.id.registration_graph) {
        appContainer.viewModelFactory
    }

    private var _binding: FragmentRegisterStep3Binding? = null
    private val binding get() = _binding!!

    private val pickOneImage = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        val target = viewModel.pendingPhotoTarget
        if (uri != null && target != null) {
            viewModel.onPhotoSelected(target, uri.toString())
        }
    }

    private val takeOnePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val target = viewModel.pendingPhotoTarget
        val uri = viewModel.pendingCameraUri
        if (success && target != null && uri != null) {
            viewModel.onPhotoSelected(target, uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRegisterStep3Binding.bind(view)

        val form = viewModel.form.value
        binding.licenseNumberEditText.setText(form.licenseNumber)
        binding.issueDateEditText.setText(form.licenseIssueDate)

        binding.licenseNumberEditText.doAfterTextChanged {
            viewModel.onLicenseNumberChanged(it?.toString().orEmpty())
        }
        binding.issueDateEditText.doAfterTextChanged {
            viewModel.onLicenseIssueDateChanged(it?.toString().orEmpty())
        }

        val openPicker = {
            showDatePicker(R.string.picker_issue_date_title, DateFormats.ISSUE_DATE_PATTERN) {
                binding.issueDateEditText.setText(it)
            }
        }
        binding.issueDateEditText.setOnClickListener { openPicker() }
        binding.issueDateLayout.setStartIconOnClickListener { openPicker() }

        binding.avatarContainer.setOnClickListener { showPhotoSourceDialog(PhotoTarget.Avatar) }
        binding.licenseUploadContainer.setOnClickListener { showPhotoSourceDialog(PhotoTarget.License) }
        binding.passportUploadContainer.setOnClickListener { showPhotoSourceDialog(PhotoTarget.Passport) }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.nextButton.setOnClickListener { viewModel.onStep3NextClicked() }

        collectWhenStarted(viewModel.form) { current ->
            binding.nextButton.isEnabled = current.isStep3Filled && !viewModel.isLoading.value
            showPhoto(binding.avatarImageView, current.avatarUri)
            showPhoto(binding.licenseImageView, current.licensePhotoUri)
            showPhoto(binding.passportImageView, current.passportPhotoUri)
            binding.licenseStatusTextView.setText(
                if (current.licensePhotoUri != null) R.string.photo_selected else R.string.photo_upload
            )
            binding.passportStatusTextView.setText(
                if (current.passportPhotoUri != null) R.string.photo_selected else R.string.photo_upload
            )
        }

        collectWhenStarted(viewModel.isLoading) { loading ->
            binding.progressIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            binding.nextButton.isEnabled = !loading && viewModel.form.value.isStep3Filled
            binding.backButton.isEnabled = !loading
        }

        collectWhenStarted(viewModel.errors) { errors ->
            binding.licenseNumberLayout.error = errors.licenseNumber?.let { getString(it) }
            binding.issueDateLayout.error = errors.issueDate?.let { getString(it) }
            binding.requiredErrorTextView.apply {
                val message = errors.requiredFields ?: errors.photos
                visibility = if (message != null) View.VISIBLE else View.GONE
                text = message?.let { getString(it) }.orEmpty()
            }
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                RegistrationEvent.Succeeded -> findNavController().navigate(R.id.action_step3_to_success)
                is RegistrationEvent.ShowError -> showErrorDialog(event.message)
                else -> Unit
            }
        }
    }

    private fun showPhotoSourceDialog(target: PhotoTarget) {
        if (viewModel.isLoading.value) return
        val options = arrayOf(getString(R.string.photo_source_gallery), getString(R.string.photo_source_camera))
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.photo_source_title)
            .setItems(options) { _, which ->
                viewModel.pendingPhotoTarget = target
                if (which == 0) openGallery() else openCamera()
            }
            .show()
    }

    private fun openGallery() {
        try {
            pickOneImage.launch(
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
            takeOnePhoto.launch(outputUri)
        } catch (e: ActivityNotFoundException) {
            showErrorDialog(getString(R.string.error_camera_unavailable))
        } catch (e: IllegalArgumentException) {
            showErrorDialog(getString(R.string.error_camera_unavailable))
        }
    }

    private fun showPhoto(imageView: ImageView, uri: String?) {
        if (uri == null) return
        if (!imageView.showImageFromUri(uri, PREVIEW_MAX_SIZE_PX)) {
            showErrorDialog(getString(R.string.error_photo_read))
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val PREVIEW_MAX_SIZE_PX = 512
    }
}

package com.example.drivenext.presentation.addcar

import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentAddCarPhotosBinding
import com.example.drivenext.presentation.common.PhotoSlotView
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.createCameraOutputUri
import com.example.drivenext.presentation.common.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddCarPhotosFragment : Fragment(R.layout.fragment_add_car_photos) {

    private val viewModel: AddCarViewModel by navGraphViewModels(R.id.add_car_graph) {
        appContainer.viewModelFactory
    }

    private var _binding: FragmentAddCarPhotosBinding? = null
    private val binding get() = _binding!!

    private val pickImages = registerForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(AddCarViewModel.MAX_PHOTOS)
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            viewModel.onPhotosAdded(uris.map { it.toString() })
        }
    }

    private val takePhoto = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        val uri = viewModel.pendingCameraUri
        if (success && uri != null) {
            viewModel.onPhotosAdded(listOf(uri))
            if (viewModel.remainingPhotoSlots > 0) {
                askForNextShot()
            }
        }
    }

    private val slots: List<PhotoSlotView>
        get() = with(binding) { listOf(slot0, slot1, slot2, slot3, slot4) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAddCarPhotosBinding.bind(view)

        slots.forEachIndexed { index, slot ->
            slot.setOnClickListener { onSlotClicked(index) }
        }

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.nextButton.setOnClickListener { viewModel.onPhotosNextClicked() }

        collectWhenStarted(viewModel.photos) { photos ->
            slots.forEachIndexed { index, slot ->
                if (!slot.showPhoto(photos.getOrNull(index), PREVIEW_MAX_SIZE_PX)) {
                    showErrorDialog(getString(R.string.error_photo_read))
                }
            }
            binding.nextButton.isEnabled = photos.isNotEmpty() && !viewModel.isLoading.value
        }

        collectWhenStarted(viewModel.isLoading) { loading ->
            binding.progressIndicator.visibility = if (loading) View.VISIBLE else View.GONE
            binding.nextButton.isEnabled = !loading && viewModel.photos.value.isNotEmpty()
            binding.backButton.isEnabled = !loading
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                AddCarEvent.Completed -> findNavController().navigate(R.id.action_photos_to_success)
                is AddCarEvent.ShowError -> showErrorDialog(event.message)
                else -> Unit
            }
        }
    }

    private fun onSlotClicked(index: Int) {
        if (viewModel.isLoading.value) return
        if (index < viewModel.photos.value.size) {
            confirmRemoval(index)
        } else {
            showSourceDialog()
        }
    }

    private fun showSourceDialog() {
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

    private fun confirmRemoval(index: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_car_photo_remove_title)
            .setPositiveButton(R.string.add_car_photo_remove) { _, _ -> viewModel.onPhotoRemoved(index) }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }

    private fun askForNextShot() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.add_car_camera_more_title)
            .setMessage(R.string.add_car_camera_more_message)
            .setPositiveButton(R.string.add_car_camera_more_yes) { _, _ -> openCamera() }
            .setNegativeButton(R.string.add_car_camera_more_no, null)
            .show()
    }

    private fun openGallery() {
        try {
            pickImages.launch(
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

    private companion object {
        const val PREVIEW_MAX_SIZE_PX = 1024
    }
}

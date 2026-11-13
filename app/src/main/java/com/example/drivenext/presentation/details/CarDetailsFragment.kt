package com.example.drivenext.presentation.details

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentCarDetailsBinding
import com.example.drivenext.presentation.cars.ARG_CAR_ID
import com.example.drivenext.presentation.cars.openRental
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.showErrorDialog

class CarDetailsFragment : Fragment(R.layout.fragment_car_details) {

    private val viewModel: CarDetailsViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentCarDetailsBinding? = null
    private val binding get() = _binding!!

    private val photoAdapter = CarPhotoPagerAdapter()

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            updatePhotoCounter(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCarDetailsBinding.bind(view)

        viewModel.start(requireArguments().getString(ARG_CAR_ID).orEmpty())

        binding.photoPager.adapter = photoAdapter
        binding.photoPager.registerOnPageChangeCallback(pageChangeCallback)

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.favouriteButton.setOnClickListener { viewModel.onFavouriteClicked() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }
        binding.bookButton.setOnClickListener {
            viewModel.state.value.car?.let { openRental(it) }
        }

        collectWhenStarted(viewModel.state) { state ->
            val car = state.car
            binding.progressIndicator.isVisible = state.isLoading && car == null
            binding.errorContainer.isVisible = state.hasError && car == null
            binding.contentScrollView.isVisible = car != null
            binding.bottomBar.isVisible = car != null
            binding.favouriteButton.isVisible = car != null
            binding.favouriteButton.isSelected = state.isFavourite
            binding.favouriteButton.contentDescription = getString(
                if (state.isFavourite) R.string.favourite_remove else R.string.favourite_add
            )

            if (car != null) {
                binding.titleCarTextView.text = car.title
                binding.addressTextView.text = car.address.ifBlank { getString(R.string.value_unknown) }
                binding.descriptionTextView.text =
                    car.description.ifBlank { getString(R.string.details_no_description) }
                binding.priceTextView.text = getString(R.string.car_price_per_day, car.pricePerDay)
                photoAdapter.submitList(car.gallery.ifEmpty { listOf("") }) {
                    updatePhotoCounter(_binding?.photoPager?.currentItem ?: 0)
                }
            }
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                is CarDetailsEvent.ShowError -> showErrorDialog(getString(event.messageRes))
            }
        }
    }

    private fun updatePhotoCounter(position: Int) {
        val currentBinding = _binding ?: return
        val total = photoAdapter.itemCount
        currentBinding.photoCounterTextView.isVisible = total > 1
        currentBinding.photoCounterTextView.text =
            getString(R.string.details_photo_counter, position + 1, total)
    }

    override fun onDestroyView() {
        binding.photoPager.unregisterOnPageChangeCallback(pageChangeCallback)
        binding.photoPager.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
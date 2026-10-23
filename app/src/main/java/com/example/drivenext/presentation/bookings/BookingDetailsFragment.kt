package com.example.drivenext.presentation.bookings

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentBookingDetailsBinding
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.BookingStatus
import com.example.drivenext.presentation.cars.bindCar
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.formatDateTime
import com.example.drivenext.presentation.common.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class BookingDetailsFragment : Fragment(R.layout.fragment_booking_details) {

    private val viewModel: BookingDetailsViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentBookingDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingDetailsBinding.bind(view)

        viewModel.start(requireArguments().getLong(BookingsFragment.ARG_BOOKING_ID))

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }
        binding.cancelButton.setOnClickListener { showCancelConfirmation() }

        collectWhenStarted(viewModel.state) { state ->
            val booking = state.booking
            binding.progressIndicator.isVisible = (state.isLoading && booking == null) || state.isCancelling
            binding.errorContainer.isVisible = state.hasError && booking == null
            binding.contentScrollView.isVisible = booking != null
            binding.cancelButton.isVisible = booking?.status == BookingStatus.Active
            binding.cancelButton.isEnabled = !state.isCancelling
            booking?.let { render(it) }
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                is BookingDetailsEvent.ShowError -> showErrorDialog(event.message)
                BookingDetailsEvent.Cancelled -> findNavController().popBackStack()
            }
        }
    }

    private fun render(booking: Booking) {
        val cost = booking.cost
        binding.titleTextView.text = getString(R.string.booking_title, booking.number)
        binding.carSummary.bindCar(
            booking.carModel,
            booking.carBrand,
            booking.pricePerDay,
            booking.carImageUrl
        )
        binding.addressRow.value = booking.address.ifBlank { getString(R.string.value_unknown) }
        binding.startRow.value = booking.startMillis.formatDateTime()
        binding.endRow.value = booking.endMillis.formatDateTime()
        binding.driverRow.value = booking.driverName
        binding.licenseRow.value = booking.licenseNumber.ifBlank { getString(R.string.value_unknown) }
        binding.statusRow.value = getString(
            when (booking.status) {
                BookingStatus.Active -> R.string.status_active
                BookingStatus.Completed -> R.string.status_completed
                BookingStatus.Cancelled -> R.string.status_cancelled
            }
        )

        val daysText = resources.getQuantityString(R.plurals.rental_days, cost.days, cost.days)
        binding.rentRow.label = getString(R.string.rental_cost_label, daysText)
        binding.rentRow.value = getString(R.string.car_price, cost.rent)
        binding.insuranceRow.label = getString(R.string.rental_insurance_label, daysText)
        binding.insuranceRow.value = getString(R.string.car_price, cost.insurance)
        binding.totalTextView.text = getString(R.string.car_price, cost.total)
    }

    private fun showCancelConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.booking_cancel_confirm_title)
            .setMessage(R.string.booking_cancel_confirm_message)
            .setPositiveButton(R.string.booking_cancel_yes) { _, _ -> viewModel.onCancelConfirmed() }
            .setNegativeButton(R.string.booking_cancel_no, null)
            .show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

package com.example.drivenext.presentation.rental

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentRentalBinding
import com.example.drivenext.presentation.cars.ARG_CAR_ID
import com.example.drivenext.presentation.cars.bindCar
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.formatDateTime
import com.example.drivenext.presentation.common.pickDateTime
import com.example.drivenext.presentation.common.showErrorDialog

class RentalFragment : Fragment(R.layout.fragment_rental) {

    private val viewModel: RentalViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentRentalBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentRentalBinding.bind(view)

        viewModel.start(requireArguments().getString(ARG_CAR_ID).orEmpty())

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }
        binding.continueButton.setOnClickListener { viewModel.onContinueClicked() }

        binding.startRow.setOnClickListener {
            val state = viewModel.state.value
            pickDateTime(
                titleRes = R.string.rental_pick_start,
                initialMillis = state.startMillis,
                minMillis = System.currentTimeMillis()
            ) { viewModel.onStartChanged(it) }
        }

        binding.endRow.setOnClickListener {
            val state = viewModel.state.value
            pickDateTime(
                titleRes = R.string.rental_pick_end,
                initialMillis = state.endMillis,
                minMillis = state.startMillis
            ) { viewModel.onEndChanged(it) }
        }

        collectWhenStarted(viewModel.state) { render(it) }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                is RentalEvent.ShowError -> showErrorDialog(event.message)
                is RentalEvent.ShowMessage -> showErrorDialog(getString(event.messageRes))
                RentalEvent.BookingCreated -> {
                    val options = NavOptions.Builder()
                        .setPopUpTo(R.id.mainFragment, false)
                        .build()
                    findNavController().navigate(R.id.rentalSuccessFragment, null, options)
                }
            }
        }
    }

    private fun render(state: RentalUiState) {
        val car = state.car
        val cost = state.cost
        binding.progressIndicator.isVisible = state.isLoading && car == null
        binding.errorContainer.isVisible = state.hasError && car == null
        binding.contentScrollView.isVisible = car != null
        binding.continueButton.isVisible = car != null
        binding.continueButton.isEnabled = !state.isSubmitting
        binding.submitIndicator.isVisible = state.isSubmitting

        if (car == null || cost == null) return

        binding.carSummary.bindCar(car.model, car.brand, car.pricePerDay, car.imageUrl)
        binding.startRow.value = state.startMillis.formatDateTime()
        binding.endRow.value = state.endMillis.formatDateTime()
        binding.addressTextView.text = car.address.ifBlank { getString(R.string.value_unknown) }

        val daysText = resources.getQuantityString(R.plurals.rental_days, cost.days, cost.days)
        binding.rentRow.label = getString(R.string.rental_cost_label, daysText)
        binding.rentRow.value = getString(R.string.car_price, cost.rent)
        binding.insuranceRow.label = getString(R.string.rental_insurance_label, daysText)
        binding.insuranceRow.value = getString(R.string.car_price, cost.insurance)
        binding.totalTextView.text = getString(R.string.car_price, cost.total)
        binding.depositTextView.text = getString(R.string.car_price, car.deposit)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}

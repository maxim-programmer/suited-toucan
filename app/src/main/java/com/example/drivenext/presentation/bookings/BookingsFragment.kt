package com.example.drivenext.presentation.bookings

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentBookingsBinding
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class BookingsFragment : Fragment(R.layout.fragment_bookings) {

    private val viewModel: BookingsViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentBookingsBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentBookingsBinding.bind(view)

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }

        val adapter = BookingAdapter { booking ->
            findNavController().navigate(
                R.id.bookingDetailsFragment,
                bundleOf(ARG_BOOKING_ID to booking.id)
            )
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        collectWhenStarted(viewModel.state) { state ->
            binding.progressIndicator.isVisible = state is BookingsState.Loading
            binding.errorContainer.isVisible = state is BookingsState.Error
            binding.recyclerView.isVisible = state is BookingsState.Content && state.bookings.isNotEmpty()
            binding.emptyTextView.isVisible = state is BookingsState.Content && state.bookings.isEmpty()
            if (state is BookingsState.Content) adapter.submitList(state.bookings)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_BOOKING_ID = "bookingId"
    }
}

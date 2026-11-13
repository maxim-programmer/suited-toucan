package com.example.drivenext.presentation.mycars

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentMyCarsBinding
import com.example.drivenext.domain.model.Car
import com.example.drivenext.presentation.cars.openCarDetails
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.common.showErrorDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MyCarsFragment : Fragment(R.layout.fragment_my_cars) {

    private val viewModel: MyCarsViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentMyCarsBinding? = null
    private val binding get() = _binding!!

    private var adapter: MyCarsAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyCarsBinding.bind(view)

        val carsAdapter = MyCarsAdapter(
            onDetailsClick = { openCarDetails(it) },
            onDeleteClick = { confirmDelete(it) }
        )
        adapter = carsAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = carsAdapter

        binding.backButton.setOnClickListener { findNavController().popBackStack() }
        binding.retryButton.setOnClickListener { viewModel.onRetryClicked() }

        collectWhenStarted(viewModel.state) { state ->
            binding.progressIndicator.isVisible = state.isLoading
            binding.errorContainer.isVisible = state.hasError
            binding.emptyTextView.isVisible =
                !state.isLoading && !state.hasError && state.cars.isEmpty()
            binding.recyclerView.isVisible = state.cars.isNotEmpty()
            carsAdapter.deletingId = state.deletingId
            carsAdapter.submitList(state.cars)
        }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                is MyCarsEvent.ShowError -> showErrorDialog(event.message)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    private fun confirmDelete(car: Car) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.my_cars_delete_title)
            .setMessage(getString(R.string.my_cars_delete_message, car.title))
            .setPositiveButton(R.string.my_cars_delete) { _, _ -> viewModel.onDeleteConfirmed(car.id) }
            .setNegativeButton(R.string.common_cancel, null)
            .show()
    }

    override fun onDestroyView() {
        binding.recyclerView.adapter = null
        adapter = null
        _binding = null
        super.onDestroyView()
    }
}
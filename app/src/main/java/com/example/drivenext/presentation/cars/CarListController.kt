package com.example.drivenext.presentation.cars

import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.example.drivenext.R
import com.example.drivenext.databinding.IncludeCarListBinding

class CarListController(
    private val binding: IncludeCarListBinding,
    private val adapter: CarAdapter,
    @StringRes emptyMessageRes: Int = R.string.cars_empty,
    @StringRes loadingMessageRes: Int = R.string.cars_loading,
    onRetry: () -> Unit
) {

    init {
        val spanCount = binding.root.resources.getInteger(R.integer.car_list_span_count)
        binding.recyclerView.layoutManager = GridLayoutManager(binding.root.context, spanCount)
        binding.recyclerView.adapter = adapter
        binding.emptyTextView.setText(emptyMessageRes)
        binding.loadingTextView.setText(loadingMessageRes)
        binding.retryButton.setOnClickListener { onRetry() }
    }

    fun render(state: CarListState) {
        binding.loadingContainer.isVisible = state is CarListState.Loading
        binding.recyclerView.isVisible = state is CarListState.Content && state.cars.isNotEmpty()
        binding.emptyTextView.isVisible = state is CarListState.Content && state.cars.isEmpty()
        binding.errorContainer.isVisible = state is CarListState.Error
        when (state) {
            is CarListState.Content -> adapter.submitList(state.cars)
            is CarListState.Error -> binding.errorTextView.setText(state.messageRes)
            CarListState.Loading -> Unit
        }
    }
}

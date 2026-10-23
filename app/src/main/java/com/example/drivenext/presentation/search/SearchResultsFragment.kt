package com.example.drivenext.presentation.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentSearchResultsBinding
import com.example.drivenext.presentation.cars.CarAdapter
import com.example.drivenext.presentation.cars.CarListController
import com.example.drivenext.presentation.cars.openCarDetails
import com.example.drivenext.presentation.cars.openRental
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class SearchResultsFragment : Fragment(R.layout.fragment_search_results) {

    private val viewModel: SearchResultsViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!

    private var carListController: CarListController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchResultsBinding.bind(view)

        binding.backButton.setOnClickListener { findNavController().popBackStack() }

        val adapter = CarAdapter(
            onBookClick = { openRental(it) },
            onDetailsClick = { openCarDetails(it) }
        )
        carListController = CarListController(binding.carList, adapter) { viewModel.onRetryClicked() }

        viewModel.onQueryReceived(requireArguments().getString(ARG_QUERY).orEmpty())

        collectWhenStarted(viewModel.state) { carListController?.render(it) }
    }

    override fun onDestroyView() {
        carListController = null
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_QUERY = "query"
    }
}

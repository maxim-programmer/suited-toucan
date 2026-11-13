package com.example.drivenext.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentMainBinding
import com.example.drivenext.presentation.cars.CarAdapter
import com.example.drivenext.presentation.cars.CarListController
import com.example.drivenext.presentation.cars.openCarDetails
import com.example.drivenext.presentation.cars.openRental
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted
import com.example.drivenext.presentation.search.SearchResultsFragment

class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModel: MainViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private var carListController: CarListController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainBinding.bind(view)

        val adapter = CarAdapter(
            onBookClick = { openRental(it) },
            onDetailsClick = { openCarDetails(it) }
        )
        carListController = CarListController(binding.carList, adapter) { viewModel.onRetryClicked() }

        setupSearchView()

        collectWhenStarted(viewModel.state) { carListController?.render(it) }

        collectWhenStarted(viewModel.events) { event ->
            when (event) {
                is MainEvent.OpenSearchResults -> findNavController().navigate(
                    R.id.searchResultsFragment,
                    bundleOf(SearchResultsFragment.ARG_QUERY to event.query)
                )
            }
        }
    }

    private fun setupSearchView() {
        val searchView = binding.searchView
        searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)?.apply {
            textSize = SEARCH_TEXT_SIZE_SP
            setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.hint))
            typeface = ResourcesCompat.getFont(requireContext(), R.font.montserrat_family)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                viewModel.onSearchSubmitted(query.orEmpty())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    override fun onStart() {
        super.onStart()
        viewModel.onScreenStarted()
    }

    override fun onDestroyView() {
        carListController = null
        _binding = null
        super.onDestroyView()
    }

    private companion object {
        const val SEARCH_TEXT_SIZE_SP = 14f
    }
}

package com.example.drivenext.presentation.favourites

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.drivenext.R
import com.example.drivenext.databinding.FragmentFavouritesBinding
import com.example.drivenext.presentation.cars.CarAdapter
import com.example.drivenext.presentation.cars.CarListController
import com.example.drivenext.presentation.cars.openCarDetails
import com.example.drivenext.presentation.cars.openRental
import com.example.drivenext.presentation.common.appContainer
import com.example.drivenext.presentation.common.collectWhenStarted

class FavouritesFragment : Fragment(R.layout.fragment_favourites) {

    private val viewModel: FavouritesViewModel by viewModels { appContainer.viewModelFactory }

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!

    private var carListController: CarListController? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavouritesBinding.bind(view)

        val adapter = CarAdapter(
            onBookClick = { openRental(it) },
            onDetailsClick = { openCarDetails(it) }
        )
        carListController = CarListController(
            binding.carList,
            adapter,
            R.string.favourites_empty,
            R.string.loading_data
        ) { viewModel.onRetryClicked() }

        collectWhenStarted(viewModel.state) { carListController?.render(it) }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refresh()
    }

    override fun onDestroyView() {
        carListController = null
        _binding = null
        super.onDestroyView()
    }
}

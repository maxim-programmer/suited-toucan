package com.example.drivenext.presentation.cars

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.domain.model.Car
import com.example.drivenext.presentation.placeholder.PlaceholderFragment

fun Fragment.openRental(car: Car) {
    findNavController().navigate(
        R.id.rentalFragment,
        bundleOf(PlaceholderFragment.ARG_SUBTITLE to "${car.brand} ${car.model}")
    )
}

fun Fragment.openCarDetails(car: Car) {
    findNavController().navigate(
        R.id.carDetailsFragment,
        bundleOf(PlaceholderFragment.ARG_SUBTITLE to "${car.brand} ${car.model}")
    )
}

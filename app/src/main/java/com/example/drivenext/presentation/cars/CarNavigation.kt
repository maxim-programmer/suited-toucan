package com.example.drivenext.presentation.cars

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.drivenext.R
import com.example.drivenext.domain.model.Car

const val ARG_CAR_ID = "carId"

fun Fragment.openRental(car: Car) {
    findNavController().navigate(R.id.rentalFragment, bundleOf(ARG_CAR_ID to car.id))
}

fun Fragment.openCarDetails(car: Car) {
    findNavController().navigate(R.id.carDetailsFragment, bundleOf(ARG_CAR_ID to car.id))
}

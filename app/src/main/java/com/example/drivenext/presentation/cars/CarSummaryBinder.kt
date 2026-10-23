package com.example.drivenext.presentation.cars

import coil.load
import com.example.drivenext.R
import com.example.drivenext.databinding.IncludeCarSummaryBinding

fun IncludeCarSummaryBinding.bindCar(
    model: String,
    brand: String,
    pricePerDay: Int,
    imageUrl: String?
) {
    summaryModelTextView.text = model
    summaryBrandTextView.text = brand
    summaryPriceTextView.text = root.context.getString(R.string.car_price, pricePerDay)
    summaryCarImageView.load(imageUrl) {
        crossfade(true)
        placeholder(R.drawable.ic_car_placeholder)
        error(R.drawable.ic_car_placeholder)
        fallback(R.drawable.ic_car_placeholder)
    }
}

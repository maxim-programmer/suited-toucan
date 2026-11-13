package com.example.drivenext.presentation.addcar

import androidx.annotation.StringRes
import com.example.drivenext.domain.model.Transmission

data class AddCarForm(
    val address: String = "",
    val year: String = "",
    val brand: String = "",
    val model: String = "",
    val transmission: Transmission? = null,
    val mileage: String = "",
    val pricePerDay: String = "",
    val description: String = ""
) {
    val isAddressFilled: Boolean get() = address.isNotBlank()

    val isDetailsFilled: Boolean
        get() = year.isNotBlank() &&
            brand.isNotBlank() &&
            model.isNotBlank() &&
            transmission != null &&
            mileage.isNotBlank() &&
            pricePerDay.isNotBlank() &&
            description.isNotBlank()
}

data class AddCarErrors(
    @StringRes val year: Int? = null,
    @StringRes val mileage: Int? = null,
    @StringRes val pricePerDay: Int? = null
)

sealed interface AddCarEvent {
    data object OpenDetails : AddCarEvent
    data object OpenPhotos : AddCarEvent
    data object Completed : AddCarEvent
    data class ShowError(val message: String) : AddCarEvent
}

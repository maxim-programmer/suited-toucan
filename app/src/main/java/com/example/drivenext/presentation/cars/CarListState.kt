package com.example.drivenext.presentation.cars

import androidx.annotation.StringRes
import com.example.drivenext.domain.model.Car

sealed interface CarListState {
    data object Loading : CarListState
    data class Content(val cars: List<Car>) : CarListState
    data class Error(@StringRes val messageRes: Int) : CarListState
}

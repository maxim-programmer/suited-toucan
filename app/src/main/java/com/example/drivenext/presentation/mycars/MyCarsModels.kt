package com.example.drivenext.presentation.mycars

import com.example.drivenext.domain.model.Car

data class MyCarsState(
    val isLoading: Boolean = true,
    val cars: List<Car> = emptyList(),
    val hasError: Boolean = false,
    val deletingId: String? = null
)

sealed interface MyCarsEvent {
    data class ShowError(val message: String) : MyCarsEvent
}
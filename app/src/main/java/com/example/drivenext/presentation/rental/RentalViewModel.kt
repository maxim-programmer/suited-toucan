package com.example.drivenext.presentation.rental

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.model.RentalCost
import com.example.drivenext.domain.usecase.CreateBookingUseCase
import com.example.drivenext.domain.usecase.GetCarUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

data class RentalUiState(
    val isLoading: Boolean = true,
    val car: Car? = null,
    val startMillis: Long = 0L,
    val endMillis: Long = 0L,
    val cost: RentalCost? = null,
    val isSubmitting: Boolean = false,
    val hasError: Boolean = false
)

sealed interface RentalEvent {
    data class ShowError(val message: String) : RentalEvent
    data class ShowMessage(@StringRes val messageRes: Int) : RentalEvent
    data object BookingCreated : RentalEvent
}

class RentalViewModel(
    private val getCar: GetCarUseCase,
    private val createBooking: CreateBookingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RentalUiState())
    val state: StateFlow<RentalUiState> = _state.asStateFlow()

    private val _events = Channel<RentalEvent>(Channel.BUFFERED)
    val events: Flow<RentalEvent> = _events.receiveAsFlow()

    private var carId: String? = null
    private var loadJob: Job? = null

    fun start(carId: String) {
        if (this.carId == carId) return
        this.carId = carId
        load()
    }

    fun onRetryClicked() {
        load()
    }

    fun onStartChanged(millis: Long) {
        _state.update { current ->
            val end = if (current.endMillis <= millis) millis + DAY_MILLIS else current.endMillis
            current.withPeriod(millis, end)
        }
    }

    fun onEndChanged(millis: Long) {
        val current = _state.value
        if (millis <= current.startMillis) {
            viewModelScope.launch { _events.send(RentalEvent.ShowMessage(R.string.error_rental_period)) }
            return
        }
        _state.update { it.withPeriod(it.startMillis, millis) }
    }

    fun onContinueClicked() {
        val current = _state.value
        val car = current.car ?: return
        if (current.isSubmitting) return
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true) }
            createBooking(car, current.startMillis, current.endMillis)
                .onSuccess {
                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(RentalEvent.BookingCreated)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSubmitting = false) }
                    _events.send(RentalEvent.ShowError(error.message ?: AppException.UNKNOWN_MESSAGE))
                }
        }
    }

    private fun load() {
        val id = carId ?: return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasError = false) }
            getCar(id)
                .onSuccess { car ->
                    _state.update { current ->
                        val start = current.startMillis.takeIf { it > 0L } ?: defaultStartMillis()
                        val end = current.endMillis.takeIf { it > start } ?: (start + DEFAULT_DAYS * DAY_MILLIS)
                        current.copy(isLoading = false, car = car, hasError = false).withPeriod(start, end)
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, hasError = true) }
                }
        }
    }

    private fun RentalUiState.withPeriod(start: Long, end: Long): RentalUiState {
        val currentCar = car
        return copy(
            startMillis = start,
            endMillis = end,
            cost = currentCar?.let {
                RentalCost.calculate(start, end, it.pricePerDay, it.insurancePerDay)
            }
        )
    }

    private fun defaultStartMillis(): Long =
        LocalDate.now()
            .plusDays(1)
            .atTime(DEFAULT_HOUR, 0)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    private companion object {
        const val DAY_MILLIS = 24L * 60L * 60L * 1000L
        const val DEFAULT_DAYS = 3L
        const val DEFAULT_HOUR = 8
    }
}

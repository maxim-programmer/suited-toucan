package com.example.drivenext.presentation.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.usecase.CancelBookingUseCase
import com.example.drivenext.domain.usecase.GetBookingUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BookingDetailsUiState(
    val isLoading: Boolean = true,
    val booking: Booking? = null,
    val isCancelling: Boolean = false,
    val hasError: Boolean = false
)

sealed interface BookingDetailsEvent {
    data class ShowError(val message: String) : BookingDetailsEvent
    data object Cancelled : BookingDetailsEvent
}

class BookingDetailsViewModel(
    private val getBooking: GetBookingUseCase,
    private val cancelBooking: CancelBookingUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(BookingDetailsUiState())
    val state: StateFlow<BookingDetailsUiState> = _state.asStateFlow()

    private val _events = Channel<BookingDetailsEvent>(Channel.BUFFERED)
    val events: Flow<BookingDetailsEvent> = _events.receiveAsFlow()

    private var bookingId: Long? = null
    private var loadJob: Job? = null

    fun start(bookingId: Long) {
        if (this.bookingId == bookingId) return
        this.bookingId = bookingId
        load()
    }

    fun onRetryClicked() {
        load()
    }

    fun onCancelConfirmed() {
        val id = bookingId ?: return
        if (_state.value.isCancelling) return
        viewModelScope.launch {
            _state.update { it.copy(isCancelling = true) }
            cancelBooking(id)
                .onSuccess {
                    _state.update { it.copy(isCancelling = false) }
                    _events.send(BookingDetailsEvent.Cancelled)
                }
                .onFailure { error ->
                    _state.update { it.copy(isCancelling = false) }
                    _events.send(BookingDetailsEvent.ShowError(error.message ?: AppException.UNKNOWN_MESSAGE))
                }
        }
    }

    private fun load() {
        val id = bookingId ?: return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasError = false) }
            getBooking(id)
                .onSuccess { booking ->
                    _state.update { it.copy(isLoading = false, booking = booking, hasError = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, hasError = true) }
                }
        }
    }
}

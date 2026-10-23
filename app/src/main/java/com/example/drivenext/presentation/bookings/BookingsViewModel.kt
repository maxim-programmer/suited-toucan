package com.example.drivenext.presentation.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.usecase.GetBookingsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface BookingsState {
    data object Loading : BookingsState
    data class Content(val bookings: List<Booking>) : BookingsState
    data object Error : BookingsState
}

class BookingsViewModel(
    private val getBookings: GetBookingsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<BookingsState>(BookingsState.Loading)
    val state: StateFlow<BookingsState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (_state.value !is BookingsState.Content) {
                _state.value = BookingsState.Loading
            }
            getBookings()
                .onSuccess { _state.value = BookingsState.Content(it) }
                .onFailure {
                    if (_state.value !is BookingsState.Content) {
                        _state.value = BookingsState.Error
                    }
                }
        }
    }

    fun onRetryClicked() {
        refresh()
    }
}

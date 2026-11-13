package com.example.drivenext.presentation.mycars

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.usecase.DeleteCarUseCase
import com.example.drivenext.domain.usecase.GetMyCarsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyCarsViewModel(
    private val getMyCars: GetMyCarsUseCase,
    private val deleteCar: DeleteCarUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MyCarsState())
    val state: StateFlow<MyCarsState> = _state.asStateFlow()

    private val _events = Channel<MyCarsEvent>(Channel.BUFFERED)
    val events: Flow<MyCarsEvent> = _events.receiveAsFlow()

    private var loadJob: Job? = null

    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (_state.value.cars.isEmpty()) {
                _state.update { it.copy(isLoading = true, hasError = false) }
            }
            getMyCars()
                .onSuccess { cars ->
                    _state.update { it.copy(isLoading = false, hasError = false, cars = cars) }
                }
                .onFailure {
                    _state.update { current ->
                        if (current.cars.isEmpty()) current.copy(isLoading = false, hasError = true)
                        else current.copy(isLoading = false)
                    }
                }
        }
    }

    fun onRetryClicked() {
        refresh()
    }

    fun onDeleteConfirmed(carId: String) {
        if (_state.value.deletingId != null) return
        viewModelScope.launch {
            _state.update { it.copy(deletingId = carId) }
            deleteCar(carId)
                .onSuccess {
                    _state.update { current ->
                        current.copy(cars = current.cars.filterNot { it.id == carId }, deletingId = null)
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(deletingId = null) }
                    _events.send(MyCarsEvent.ShowError(error.message ?: AppException.UNKNOWN_MESSAGE))
                }
        }
    }
}
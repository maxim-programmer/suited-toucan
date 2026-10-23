package com.example.drivenext.presentation.details

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.usecase.GetCarUseCase
import com.example.drivenext.domain.usecase.IsCarFavouriteUseCase
import com.example.drivenext.domain.usecase.ToggleFavouriteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CarDetailsUiState(
    val isLoading: Boolean = true,
    val car: Car? = null,
    val isFavourite: Boolean = false,
    val hasError: Boolean = false
)

sealed interface CarDetailsEvent {
    data class ShowError(@StringRes val messageRes: Int) : CarDetailsEvent
}

class CarDetailsViewModel(
    private val getCar: GetCarUseCase,
    private val isCarFavourite: IsCarFavouriteUseCase,
    private val toggleFavourite: ToggleFavouriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(CarDetailsUiState())
    val state: StateFlow<CarDetailsUiState> = _state.asStateFlow()

    private val _events = Channel<CarDetailsEvent>(Channel.BUFFERED)
    val events: Flow<CarDetailsEvent> = _events.receiveAsFlow()

    private var carId: String? = null
    private var loadJob: Job? = null
    private var favouriteJob: Job? = null

    fun start(carId: String) {
        if (this.carId == carId) return
        this.carId = carId
        load()
    }

    fun onRetryClicked() {
        load()
    }

    fun onFavouriteClicked() {
        val car = _state.value.car ?: return
        if (favouriteJob?.isActive == true) return
        favouriteJob = viewModelScope.launch {
            toggleFavourite(car.id)
                .onSuccess { isFavourite -> _state.update { it.copy(isFavourite = isFavourite) } }
                .onFailure { _events.send(CarDetailsEvent.ShowError(R.string.error_favourite)) }
        }
    }

    private fun load() {
        val id = carId ?: return
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasError = false) }
            getCar(id)
                .onSuccess { car ->
                    val favourite = isCarFavourite(car.id).getOrDefault(false)
                    _state.update {
                        it.copy(isLoading = false, car = car, isFavourite = favourite, hasError = false)
                    }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, hasError = true) }
                }
        }
    }
}

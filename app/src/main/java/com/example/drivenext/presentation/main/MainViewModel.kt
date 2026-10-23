package com.example.drivenext.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.usecase.GetCarsUseCase
import com.example.drivenext.presentation.cars.CarListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed interface MainEvent {
    data class OpenSearchResults(val query: String) : MainEvent
}

class MainViewModel(
    private val getCars: GetCarsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CarListState>(CarListState.Loading)
    val state: StateFlow<CarListState> = _state.asStateFlow()

    private val _events = Channel<MainEvent>(Channel.BUFFERED)
    val events: Flow<MainEvent> = _events.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        loadCars()
    }

    fun onRetryClicked() {
        loadCars()
    }

    fun onSearchSubmitted(query: String) {
        val normalized = query.trim()
        if (normalized.isEmpty()) return
        viewModelScope.launch { _events.send(MainEvent.OpenSearchResults(normalized)) }
    }

    private fun loadCars() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _state.value = CarListState.Loading
            getCars()
                .onSuccess { _state.value = CarListState.Content(it) }
                .onFailure { _state.value = CarListState.Error(R.string.error_load_data) }
        }
    }
}

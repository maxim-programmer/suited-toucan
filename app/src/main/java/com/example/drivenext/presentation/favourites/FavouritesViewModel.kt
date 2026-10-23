package com.example.drivenext.presentation.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.usecase.GetFavouriteCarsUseCase
import com.example.drivenext.presentation.cars.CarListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavouritesViewModel(
    private val getFavouriteCars: GetFavouriteCarsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CarListState>(CarListState.Loading)
    val state: StateFlow<CarListState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            if (_state.value !is CarListState.Content) {
                _state.value = CarListState.Loading
            }
            getFavouriteCars()
                .onSuccess { _state.value = CarListState.Content(it) }
                .onFailure {
                    if (_state.value !is CarListState.Content) {
                        _state.value = CarListState.Error(R.string.error_load_data)
                    }
                }
        }
    }

    fun onRetryClicked() {
        refresh()
    }
}

package com.example.drivenext.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.usecase.SearchCarsUseCase
import com.example.drivenext.presentation.cars.CarListState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchResultsViewModel(
    private val searchCars: SearchCarsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<CarListState>(CarListState.Loading)
    val state: StateFlow<CarListState> = _state.asStateFlow()

    private var currentQuery: String? = null
    private var searchJob: Job? = null

    fun onQueryReceived(query: String) {
        if (query == currentQuery) return
        currentQuery = query
        search()
    }

    fun onRetryClicked() {
        search()
    }

    private fun search() {
        val query = currentQuery ?: return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _state.value = CarListState.Loading
            searchCars(query)
                .onSuccess { _state.value = CarListState.Content(it) }
                .onFailure { _state.value = CarListState.Error(R.string.error_search) }
        }
    }
}

package com.example.drivenext.presentation.noconnection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.StartDestination
import com.example.drivenext.domain.usecase.ResolveStartDestinationUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class NoConnectionViewModel(
    private val resolveStartDestination: ResolveStartDestinationUseCase
) : ViewModel() {

    private val _isChecking = MutableStateFlow(false)
    val isChecking: StateFlow<Boolean> = _isChecking.asStateFlow()

    private val _events = Channel<StartDestination>(Channel.BUFFERED)
    val events: Flow<StartDestination> = _events.receiveAsFlow()

    fun onRetryClicked() {
        if (_isChecking.value) return
        viewModelScope.launch {
            _isChecking.value = true
            delay(CHECK_FEEDBACK_DELAY_MS)
            val destination = resolveStartDestination()
            _isChecking.value = false
            if (destination != StartDestination.NoConnection) {
                _events.send(destination)
            }
        }
    }

    private companion object {
        const val CHECK_FEEDBACK_DELAY_MS = 600L
    }
}

package com.example.drivenext.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.StartDestination
import com.example.drivenext.domain.usecase.ResolveStartDestinationUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val resolveStartDestination: ResolveStartDestinationUseCase
) : ViewModel() {

    private val _events = Channel<StartDestination>(Channel.BUFFERED)
    val events: Flow<StartDestination> = _events.receiveAsFlow()

    init {
        viewModelScope.launch {
            val destination = coroutineScope {
                val minimumDelay = async { delay(SPLASH_DURATION_MS) }
                val resolved = async { resolveStartDestination() }
                minimumDelay.await()
                resolved.await()
            }
            _events.send(destination)
        }
    }

    private companion object {
        const val SPLASH_DURATION_MS = 2500L
    }
}

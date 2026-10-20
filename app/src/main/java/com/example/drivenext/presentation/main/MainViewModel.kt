package com.example.drivenext.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.usecase.SignOutUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val signOut: SignOutUseCase
) : ViewModel() {

    private val _events = Channel<Unit>(Channel.BUFFERED)
    val events: Flow<Unit> = _events.receiveAsFlow()

    fun onLogoutClicked() {
        viewModelScope.launch {
            signOut()
            _events.send(Unit)
        }
    }
}

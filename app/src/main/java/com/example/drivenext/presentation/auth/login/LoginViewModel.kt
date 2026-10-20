package com.example.drivenext.presentation.auth.login

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.usecase.ObserveAuthStateUseCase
import com.example.drivenext.domain.usecase.ResetPasswordUseCase
import com.example.drivenext.domain.usecase.SignInUseCase
import com.example.drivenext.domain.usecase.SignInWithGoogleUseCase
import com.example.drivenext.domain.validation.Validators
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    @StringRes val emailError: Int? = null
) {
    val isLoginEnabled: Boolean
        get() = email.isNotBlank() && password.isNotEmpty() && !isLoading
}

sealed interface LoginEvent {
    data object NavigateToMain : LoginEvent
    data class ShowError(val message: String) : LoginEvent
    data class ShowInfo(@StringRes val titleRes: Int, @StringRes val messageRes: Int) : LoginEvent
}

class LoginViewModel(
    private val signIn: SignInUseCase,
    private val signInWithGoogle: SignInWithGoogleUseCase,
    private val resetPassword: ResetPasswordUseCase,
    observeAuthState: ObserveAuthStateUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events: Flow<LoginEvent> = _events.receiveAsFlow()

    private var awaitingExternalAuth = false

    init {
        viewModelScope.launch {
            observeAuthState()
                .filter { it }
                .collect {
                    if (awaitingExternalAuth) {
                        awaitingExternalAuth = false
                        _state.update { state -> state.copy(isLoading = false) }
                        _events.send(LoginEvent.NavigateToMain)
                    }
                }
        }
    }

    fun onEmailChanged(value: String) {
        _state.update { it.copy(email = value, emailError = null) }
    }

    fun onPasswordChanged(value: String) {
        _state.update { it.copy(password = value) }
    }

    fun onLoginClicked() {
        val current = _state.value
        if (current.isLoading) return
        if (!Validators.isEmailValid(current.email)) {
            _state.update { it.copy(emailError = R.string.error_invalid_email) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = signIn(current.email.trim(), current.password)
            _state.update { it.copy(isLoading = false) }
            result
                .onSuccess { _events.send(LoginEvent.NavigateToMain) }
                .onFailure { _events.send(LoginEvent.ShowError(it.toUserMessage())) }
        }
    }

    fun onGoogleClicked() {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            awaitingExternalAuth = true
            signInWithGoogle().onFailure {
                awaitingExternalAuth = false
                _events.send(LoginEvent.ShowError(it.toUserMessage()))
            }
        }
    }

    fun onForgotPasswordClicked() {
        val current = _state.value
        if (current.isLoading) return
        if (!Validators.isEmailValid(current.email)) {
            _state.update { it.copy(emailError = R.string.error_invalid_email) }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = resetPassword(current.email.trim())
            _state.update { it.copy(isLoading = false) }
            result
                .onSuccess {
                    _events.send(
                        LoginEvent.ShowInfo(
                            R.string.reset_password_title,
                            R.string.reset_password_message
                        )
                    )
                }
                .onFailure { _events.send(LoginEvent.ShowError(it.toUserMessage())) }
        }
    }

    private fun Throwable.toUserMessage(): String = message ?: AppException.UNKNOWN_MESSAGE
}

package com.example.drivenext.presentation.profile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.UserProfile
import com.example.drivenext.domain.usecase.GetCurrentProfileUseCase
import com.example.drivenext.domain.usecase.ResetPasswordUseCase
import com.example.drivenext.domain.usecase.SignOutUseCase
import com.example.drivenext.domain.usecase.UpdateAvatarUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val profile: UserProfile? = null,
    val hasError: Boolean = false
)

sealed interface ProfileEvent {
    data object LoggedOut : ProfileEvent
    data class ShowError(val message: String) : ProfileEvent
    data class ShowInfo(@StringRes val titleRes: Int, @StringRes val messageRes: Int) : ProfileEvent
}

class ProfileViewModel(
    private val getCurrentProfile: GetCurrentProfileUseCase,
    private val updateAvatar: UpdateAvatarUseCase,
    private val signOut: SignOutUseCase,
    private val resetPassword: ResetPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events: Flow<ProfileEvent> = _events.receiveAsFlow()

    var pendingCameraUri: String? = null

    private var isSigningOut = false

    init {
        loadProfile()
    }

    fun onRetryClicked() {
        loadProfile()
    }

    fun onAvatarSelected(uri: String) {
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            updateAvatar(uri)
                .onSuccess { url ->
                    _state.update { current ->
                        current.copy(
                            isLoading = false,
                            profile = current.profile?.let { profile -> profile.copy(avatarUrl = url ?: profile.avatarUrl) }
                        )
                    }
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false) }
                    _events.send(ProfileEvent.ShowError(error.message ?: AppException.UNKNOWN_MESSAGE))
                }
        }
    }

    fun onChangePasswordClicked() {
        val email = _state.value.profile?.email ?: return
        if (_state.value.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = resetPassword(email)
            _state.update { it.copy(isLoading = false) }
            result
                .onSuccess {
                    _events.send(
                        ProfileEvent.ShowInfo(
                            R.string.reset_password_title,
                            R.string.reset_password_message
                        )
                    )
                }
                .onFailure {
                    _events.send(ProfileEvent.ShowError(it.message ?: AppException.UNKNOWN_MESSAGE))
                }
        }
    }

    fun onLogoutClicked() {
        if (isSigningOut) return
        isSigningOut = true
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = signOut()
            _state.update { it.copy(isLoading = false) }
            isSigningOut = false
            result
                .onSuccess { _events.send(ProfileEvent.LoggedOut) }
                .onFailure {
                    _events.send(ProfileEvent.ShowError(it.message ?: AppException.UNKNOWN_MESSAGE))
                }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, hasError = false) }
            getCurrentProfile()
                .onSuccess { profile ->
                    _state.update { it.copy(isLoading = false, profile = profile, hasError = false) }
                }
                .onFailure {
                    _state.update { it.copy(isLoading = false, hasError = true) }
                }
        }
    }
}

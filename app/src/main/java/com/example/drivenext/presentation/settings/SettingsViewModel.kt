package com.example.drivenext.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.domain.model.ThemeMode
import com.example.drivenext.domain.model.UserProfile
import com.example.drivenext.domain.usecase.GetCurrentProfileUseCase
import com.example.drivenext.domain.usecase.GetThemeModeUseCase
import com.example.drivenext.domain.usecase.SetThemeModeUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = false,
    val profile: UserProfile? = null,
    val hasError: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.System
)

class SettingsViewModel(
    private val getCurrentProfile: GetCurrentProfileUseCase,
    private val getThemeMode: GetThemeModeUseCase,
    private val setThemeMode: SetThemeModeUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState(themeMode = getThemeMode()))
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    private var loadJob: Job? = null

    fun refresh() {
        if (loadJob?.isActive == true) return
        loadJob = viewModelScope.launch {
            val hasProfile = _state.value.profile != null
            _state.update { it.copy(isLoading = !hasProfile, hasError = false) }
            getCurrentProfile()
                .onSuccess { profile ->
                    _state.update { it.copy(isLoading = false, profile = profile, hasError = false) }
                }
                .onFailure {
                    _state.update { current ->
                        current.copy(isLoading = false, hasError = current.profile == null)
                    }
                }
        }
    }

    fun onRetryClicked() {
        refresh()
    }

    fun onThemeSelected(mode: ThemeMode) {
        setThemeMode(mode)
        _state.update { it.copy(themeMode = mode) }
    }
}

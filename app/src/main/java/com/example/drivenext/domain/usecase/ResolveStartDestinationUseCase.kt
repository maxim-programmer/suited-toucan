package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.StartDestination
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.ConnectivityRepository
import com.example.drivenext.domain.repository.SettingsRepository

class ResolveStartDestinationUseCase(
    private val connectivityRepository: ConnectivityRepository,
    private val settingsRepository: SettingsRepository,
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(): StartDestination = when {
        !connectivityRepository.isInternetAvailable() -> StartDestination.NoConnection
        !settingsRepository.isOnboardingCompleted() -> StartDestination.Onboarding
        authRepository.hasActiveSession() -> StartDestination.Main
        else -> StartDestination.Auth
    }
}

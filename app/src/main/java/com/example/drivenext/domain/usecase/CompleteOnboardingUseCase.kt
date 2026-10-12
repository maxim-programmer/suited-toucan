package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.repository.SettingsRepository

class CompleteOnboardingUseCase(
    private val settingsRepository: SettingsRepository
) {

    operator fun invoke() {
        settingsRepository.setOnboardingCompleted()
    }
}

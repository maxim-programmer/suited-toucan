package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.ThemeMode
import com.example.drivenext.domain.repository.SettingsRepository

class SetThemeModeUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke(mode: ThemeMode) {
        settingsRepository.setThemeMode(mode)
    }
}

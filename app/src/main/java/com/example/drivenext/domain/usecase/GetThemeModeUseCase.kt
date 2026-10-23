package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.ThemeMode
import com.example.drivenext.domain.repository.SettingsRepository

class GetThemeModeUseCase(private val settingsRepository: SettingsRepository) {

    operator fun invoke(): ThemeMode = settingsRepository.getThemeMode()
}

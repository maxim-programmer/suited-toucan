package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.ThemeMode

interface SettingsRepository {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted()
    fun getThemeMode(): ThemeMode
    fun setThemeMode(mode: ThemeMode)
}

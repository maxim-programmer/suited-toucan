package com.example.drivenext.domain.repository

interface SettingsRepository {
    fun isOnboardingCompleted(): Boolean
    fun setOnboardingCompleted()
}

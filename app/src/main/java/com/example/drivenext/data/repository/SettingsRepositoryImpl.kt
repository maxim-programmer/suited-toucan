package com.example.drivenext.data.repository

import android.content.Context
import com.example.drivenext.domain.repository.SettingsRepository

class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun isOnboardingCompleted(): Boolean =
        runCatching { preferences.getBoolean(KEY_ONBOARDING_COMPLETED, false) }
            .getOrDefault(false)

    override fun setOnboardingCompleted() {
        runCatching {
            preferences.edit().putBoolean(KEY_ONBOARDING_COMPLETED, true).apply()
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "drivenext_settings"
        const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
    }
}

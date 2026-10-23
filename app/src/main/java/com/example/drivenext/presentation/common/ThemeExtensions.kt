package com.example.drivenext.presentation.common

import androidx.appcompat.app.AppCompatDelegate
import com.example.drivenext.domain.model.ThemeMode

fun ThemeMode.toNightMode(): Int = when (this) {
    ThemeMode.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    ThemeMode.Light -> AppCompatDelegate.MODE_NIGHT_NO
    ThemeMode.Dark -> AppCompatDelegate.MODE_NIGHT_YES
}

fun applyThemeMode(mode: ThemeMode) {
    AppCompatDelegate.setDefaultNightMode(mode.toNightMode())
}

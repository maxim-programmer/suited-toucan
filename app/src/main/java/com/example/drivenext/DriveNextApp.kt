package com.example.drivenext

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.drivenext.di.AppContainer
import com.example.drivenext.presentation.common.toNightMode

class DriveNextApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        AppCompatDelegate.setDefaultNightMode(container.getThemeModeUseCase().toNightMode())
    }
}

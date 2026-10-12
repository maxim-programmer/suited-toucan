package com.example.drivenext

import android.app.Application
import com.example.drivenext.di.AppContainer

class DriveNextApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

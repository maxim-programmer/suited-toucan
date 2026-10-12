package com.example.drivenext.di

import android.content.Context
import com.example.drivenext.data.repository.ConnectivityRepositoryImpl
import com.example.drivenext.data.repository.SettingsRepositoryImpl
import com.example.drivenext.domain.repository.ConnectivityRepository
import com.example.drivenext.domain.repository.SettingsRepository
import com.example.drivenext.domain.usecase.CompleteOnboardingUseCase
import com.example.drivenext.domain.usecase.ResolveStartDestinationUseCase
import com.example.drivenext.presentation.common.ViewModelFactory

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val settingsRepository: SettingsRepository =
        SettingsRepositoryImpl(appContext)

    private val connectivityRepository: ConnectivityRepository =
        ConnectivityRepositoryImpl(appContext)

    val resolveStartDestinationUseCase =
        ResolveStartDestinationUseCase(connectivityRepository, settingsRepository)

    val completeOnboardingUseCase =
        CompleteOnboardingUseCase(settingsRepository)

    val viewModelFactory = ViewModelFactory(this)
}

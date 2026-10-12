package com.example.drivenext.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drivenext.di.AppContainer
import com.example.drivenext.presentation.noconnection.NoConnectionViewModel
import com.example.drivenext.presentation.onboarding.OnboardingViewModel
import com.example.drivenext.presentation.splash.SplashViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(SplashViewModel::class.java) ->
            SplashViewModel(container.resolveStartDestinationUseCase) as T
        modelClass.isAssignableFrom(NoConnectionViewModel::class.java) ->
            NoConnectionViewModel(container.resolveStartDestinationUseCase) as T
        modelClass.isAssignableFrom(OnboardingViewModel::class.java) ->
            OnboardingViewModel(container.completeOnboardingUseCase) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

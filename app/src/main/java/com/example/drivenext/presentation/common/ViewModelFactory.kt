package com.example.drivenext.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drivenext.di.AppContainer
import com.example.drivenext.presentation.auth.login.LoginViewModel
import com.example.drivenext.presentation.auth.register.RegistrationViewModel
import com.example.drivenext.presentation.main.MainViewModel
import com.example.drivenext.presentation.noconnection.NoConnectionViewModel
import com.example.drivenext.presentation.onboarding.OnboardingViewModel
import com.example.drivenext.presentation.profile.ProfileViewModel
import com.example.drivenext.presentation.search.SearchResultsViewModel
import com.example.drivenext.presentation.settings.SettingsViewModel
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
        modelClass.isAssignableFrom(LoginViewModel::class.java) ->
            LoginViewModel(
                container.signInUseCase,
                container.signInWithGoogleUseCase,
                container.resetPasswordUseCase,
                container.observeAuthStateUseCase
            ) as T
        modelClass.isAssignableFrom(RegistrationViewModel::class.java) ->
            RegistrationViewModel(container.registerUserUseCase) as T
        modelClass.isAssignableFrom(MainViewModel::class.java) ->
            MainViewModel(container.getCarsUseCase) as T
        modelClass.isAssignableFrom(SearchResultsViewModel::class.java) ->
            SearchResultsViewModel(container.searchCarsUseCase) as T
        modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
            SettingsViewModel(container.getCurrentProfileUseCase) as T
        modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
            ProfileViewModel(
                container.getCurrentProfileUseCase,
                container.updateAvatarUseCase,
                container.signOutUseCase,
                container.resetPasswordUseCase
            ) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}

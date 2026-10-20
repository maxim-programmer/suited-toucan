package com.example.drivenext.di

import android.content.Context
import com.example.drivenext.data.remote.AuthDeepLinkHandler
import com.example.drivenext.data.remote.SupabaseClientFactory
import com.example.drivenext.data.repository.AuthRepositoryImpl
import com.example.drivenext.data.repository.ConnectivityRepositoryImpl
import com.example.drivenext.data.repository.ProfileRepositoryImpl
import com.example.drivenext.data.repository.SettingsRepositoryImpl
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.ConnectivityRepository
import com.example.drivenext.domain.repository.ProfileRepository
import com.example.drivenext.domain.repository.SettingsRepository
import com.example.drivenext.domain.usecase.CompleteOnboardingUseCase
import com.example.drivenext.domain.usecase.ObserveAuthStateUseCase
import com.example.drivenext.domain.usecase.RegisterUserUseCase
import com.example.drivenext.domain.usecase.ResetPasswordUseCase
import com.example.drivenext.domain.usecase.ResolveStartDestinationUseCase
import com.example.drivenext.domain.usecase.SignInUseCase
import com.example.drivenext.domain.usecase.SignInWithGoogleUseCase
import com.example.drivenext.domain.usecase.SignOutUseCase
import com.example.drivenext.presentation.common.ViewModelFactory
import io.github.jan.supabase.SupabaseClient

class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    private val supabaseClient: SupabaseClient by lazy { SupabaseClientFactory.create() }

    private val settingsRepository: SettingsRepository =
        SettingsRepositoryImpl(appContext)

    private val connectivityRepository: ConnectivityRepository =
        ConnectivityRepositoryImpl(appContext)

    private val authRepository: AuthRepository =
        AuthRepositoryImpl { supabaseClient }

    private val profileRepository: ProfileRepository =
        ProfileRepositoryImpl(appContext) { supabaseClient }

    val authDeepLinkHandler = AuthDeepLinkHandler { supabaseClient }

    val resolveStartDestinationUseCase =
        ResolveStartDestinationUseCase(connectivityRepository, settingsRepository, authRepository)

    val completeOnboardingUseCase =
        CompleteOnboardingUseCase(settingsRepository)

    val signInUseCase = SignInUseCase(authRepository)

    val signInWithGoogleUseCase = SignInWithGoogleUseCase(authRepository)

    val resetPasswordUseCase = ResetPasswordUseCase(authRepository)

    val observeAuthStateUseCase = ObserveAuthStateUseCase(authRepository)

    val signOutUseCase = SignOutUseCase(authRepository)

    val registerUserUseCase = RegisterUserUseCase(authRepository, profileRepository)

    val viewModelFactory = ViewModelFactory(this)
}

package com.example.drivenext.presentation.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.drivenext.di.AppContainer
import com.example.drivenext.presentation.addcar.AddCarViewModel
import com.example.drivenext.presentation.auth.login.LoginViewModel
import com.example.drivenext.presentation.auth.register.RegistrationViewModel
import com.example.drivenext.presentation.bookings.BookingDetailsViewModel
import com.example.drivenext.presentation.bookings.BookingsViewModel
import com.example.drivenext.presentation.details.CarDetailsViewModel
import com.example.drivenext.presentation.favourites.FavouritesViewModel
import com.example.drivenext.presentation.help.HelpViewModel
import com.example.drivenext.presentation.main.MainViewModel
import com.example.drivenext.presentation.mycars.MyCarsViewModel
import com.example.drivenext.presentation.noconnection.NoConnectionViewModel
import com.example.drivenext.presentation.onboarding.OnboardingViewModel
import com.example.drivenext.presentation.profile.ProfileViewModel
import com.example.drivenext.presentation.rental.RentalViewModel
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
            SettingsViewModel(
                container.getCurrentProfileUseCase,
                container.getThemeModeUseCase,
                container.setThemeModeUseCase
            ) as T
        modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
            ProfileViewModel(
                container.getCurrentProfileUseCase,
                container.updateAvatarUseCase,
                container.updateGenderUseCase,
                container.updateNameUseCase,
                container.changePasswordUseCase,
                container.signOutUseCase
            ) as T
        modelClass.isAssignableFrom(CarDetailsViewModel::class.java) ->
            CarDetailsViewModel(
                container.getCarUseCase,
                container.isCarFavouriteUseCase,
                container.toggleFavouriteUseCase
            ) as T
        modelClass.isAssignableFrom(RentalViewModel::class.java) ->
            RentalViewModel(container.getCarUseCase, container.createBookingUseCase) as T
        modelClass.isAssignableFrom(BookingsViewModel::class.java) ->
            BookingsViewModel(container.getBookingsUseCase) as T
        modelClass.isAssignableFrom(BookingDetailsViewModel::class.java) ->
            BookingDetailsViewModel(container.getBookingUseCase, container.cancelBookingUseCase) as T
        modelClass.isAssignableFrom(FavouritesViewModel::class.java) ->
            FavouritesViewModel(container.getFavouriteCarsUseCase) as T
        modelClass.isAssignableFrom(AddCarViewModel::class.java) ->
            AddCarViewModel(container.addCarUseCase, container.uploadCarPhotosUseCase) as T
        modelClass.isAssignableFrom(HelpViewModel::class.java) ->
            HelpViewModel() as T
        modelClass.isAssignableFrom(MyCarsViewModel::class.java) ->
            MyCarsViewModel(container.getMyCarsUseCase, container.deleteCarUseCase) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
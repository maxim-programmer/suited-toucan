package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.model.NewBooking
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.domain.repository.ProfileRepository
import com.example.drivenext.domain.util.suspendRunCatching

class CreateBookingUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val bookingRepository: BookingRepository
) {

    suspend operator fun invoke(car: Car, startMillis: Long, endMillis: Long): Result<Long> =
        suspendRunCatching {
            if (endMillis <= startMillis) throw AppException(INVALID_PERIOD_MESSAGE)
            if (startMillis < System.currentTimeMillis()) throw AppException(PAST_START_MESSAGE)

            val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
            val profile = try {
                profileRepository.loadProfile(user)
            } catch (e: AppException) {
                null
            }

            bookingRepository.createBooking(
                userId = user.id,
                booking = NewBooking(
                    car = car,
                    startMillis = startMillis,
                    endMillis = endMillis,
                    driverName = profile?.fullName?.takeIf { it.isNotBlank() } ?: user.email,
                    licenseNumber = profile?.licenseNumber.orEmpty()
                )
            )
        }

    private companion object {
        const val INVALID_PERIOD_MESSAGE = "Дата окончания аренды должна быть позже даты начала."
        const val PAST_START_MESSAGE = "Дата начала аренды не может быть в прошлом."
    }
}

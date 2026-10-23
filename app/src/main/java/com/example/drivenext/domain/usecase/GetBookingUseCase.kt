package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.domain.util.suspendRunCatching

class GetBookingUseCase(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) {

    suspend operator fun invoke(id: Long): Result<Booking> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        bookingRepository.getBooking(user.id, id) ?: throw AppException(NOT_FOUND_MESSAGE)
    }

    private companion object {
        const val NOT_FOUND_MESSAGE = "Бронирование не найдено."
    }
}

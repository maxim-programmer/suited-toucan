package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.domain.util.suspendRunCatching

class GetBookingsUseCase(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) {

    suspend operator fun invoke(): Result<List<Booking>> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        bookingRepository.getBookings(user.id)
    }
}

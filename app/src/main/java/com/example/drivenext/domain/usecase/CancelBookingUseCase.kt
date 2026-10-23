package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.BookingRepository
import com.example.drivenext.domain.util.suspendRunCatching

class CancelBookingUseCase(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository
) {

    suspend operator fun invoke(id: Long): Result<Unit> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        if (!bookingRepository.cancelBooking(user.id, id)) {
            throw AppException(CANCEL_FAILED_MESSAGE)
        }
    }

    private companion object {
        const val CANCEL_FAILED_MESSAGE = "Не удалось отменить бронирование. Попробуйте снова."
    }
}

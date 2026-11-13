package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.CarListingRepository
import com.example.drivenext.domain.util.suspendRunCatching

class DeleteCarUseCase(
    private val authRepository: AuthRepository,
    private val carListingRepository: CarListingRepository
) {

    suspend operator fun invoke(carId: String): Result<Unit> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        carListingRepository.deleteCar(user.id, carId)
    }
}
package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.NewCar
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.CarListingRepository
import com.example.drivenext.domain.util.suspendRunCatching

class AddCarUseCase(
    private val authRepository: AuthRepository,
    private val carListingRepository: CarListingRepository
) {

    suspend operator fun invoke(existingCarId: String?, car: NewCar): Result<String> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        carListingRepository.saveCar(user.id, existingCarId, car)
    }
}

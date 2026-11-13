package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.util.suspendRunCatching

class GetMyCarsUseCase(
    private val authRepository: AuthRepository,
    private val carRepository: CarRepository
) {

    suspend operator fun invoke(): Result<List<Car>> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        carRepository.getCarsByOwner(user.id)
    }
}
package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.util.suspendRunCatching

class GetCarUseCase(private val carRepository: CarRepository) {

    suspend operator fun invoke(id: String): Result<Car> = suspendRunCatching {
        carRepository.getCar(id) ?: throw AppException(CAR_NOT_FOUND_MESSAGE)
    }

    private companion object {
        const val CAR_NOT_FOUND_MESSAGE = "Автомобиль не найден."
    }
}

package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.util.suspendRunCatching

class GetCarsUseCase(private val carRepository: CarRepository) {

    suspend operator fun invoke(): Result<List<Car>> =
        suspendRunCatching { carRepository.getCars() }
}

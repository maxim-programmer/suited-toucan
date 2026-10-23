package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.util.suspendRunCatching

class SearchCarsUseCase(private val carRepository: CarRepository) {

    suspend operator fun invoke(query: String): Result<List<Car>> = suspendRunCatching {
        val normalized = query.trim()
        if (normalized.isEmpty()) carRepository.getCars() else carRepository.searchCars(normalized)
    }
}

package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.CarRepository
import com.example.drivenext.domain.repository.FavouriteRepository
import com.example.drivenext.domain.util.suspendRunCatching

class GetFavouriteCarsUseCase(
    private val authRepository: AuthRepository,
    private val favouriteRepository: FavouriteRepository,
    private val carRepository: CarRepository
) {

    suspend operator fun invoke(): Result<List<Car>> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        val ids = favouriteRepository.getFavouriteIds(user.id)
        if (ids.isEmpty()) {
            emptyList()
        } else {
            val carsById = carRepository.getCarsByIds(ids).associateBy { it.id }
            ids.mapNotNull { carsById[it] }
        }
    }
}

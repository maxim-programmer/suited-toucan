package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.FavouriteRepository
import com.example.drivenext.domain.util.suspendRunCatching

class IsCarFavouriteUseCase(
    private val authRepository: AuthRepository,
    private val favouriteRepository: FavouriteRepository
) {

    suspend operator fun invoke(carId: String): Result<Boolean> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        favouriteRepository.isFavourite(user.id, carId)
    }
}

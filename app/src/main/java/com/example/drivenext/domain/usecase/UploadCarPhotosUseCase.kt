package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.CarListingRepository
import com.example.drivenext.domain.util.suspendRunCatching

class UploadCarPhotosUseCase(
    private val authRepository: AuthRepository,
    private val carListingRepository: CarListingRepository
) {

    suspend operator fun invoke(carId: String, uris: List<String>): Result<Unit> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        carListingRepository.uploadPhotos(user.id, carId, uris)
    }
}

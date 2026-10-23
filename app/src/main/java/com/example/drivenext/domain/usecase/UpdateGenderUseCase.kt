package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Gender
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.ProfileRepository
import com.example.drivenext.domain.util.suspendRunCatching

class UpdateGenderUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(gender: Gender): Result<Unit> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        profileRepository.updateGender(user.id, gender)
    }
}

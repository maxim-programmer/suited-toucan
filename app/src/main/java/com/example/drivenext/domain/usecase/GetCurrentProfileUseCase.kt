package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.UserProfile
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.ProfileRepository
import com.example.drivenext.domain.util.suspendRunCatching

class GetCurrentProfileUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(): Result<UserProfile> = suspendRunCatching {
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        profileRepository.loadProfile(user)
    }
}

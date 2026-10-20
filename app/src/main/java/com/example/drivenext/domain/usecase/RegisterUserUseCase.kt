package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.RegistrationData
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.ProfileRepository
import com.example.drivenext.domain.util.suspendRunCatching

class RegisterUserUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(data: RegistrationData): Result<Unit> = suspendRunCatching {
        val existing = authRepository.currentUser()
        val user = if (existing != null && existing.email.equals(data.email, ignoreCase = true)) {
            existing
        } else {
            authRepository.signUp(data.email, data.password)
            authRepository.currentUser()
                ?: throw AppException("Не удалось создать учётную запись. Попробуйте ещё раз.")
        }
        profileRepository.saveProfile(user.id, data)
    }
}

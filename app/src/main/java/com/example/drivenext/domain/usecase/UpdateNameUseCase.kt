package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.repository.ProfileRepository
import com.example.drivenext.domain.util.suspendRunCatching
import com.example.drivenext.domain.validation.NameValidator

class UpdateNameUseCase(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) {

    suspend operator fun invoke(firstName: String, lastName: String): Result<Unit> = suspendRunCatching {
        if (!NameValidator.isNameValid(firstName) || !NameValidator.isNameValid(lastName)) {
            throw AppException(INVALID_NAME_MESSAGE)
        }
        val user = authRepository.currentUser() ?: throw AppException(AppException.SESSION_MESSAGE)
        profileRepository.updateName(user.id, firstName.trim(), lastName.trim())
    }

    private companion object {
        const val INVALID_NAME_MESSAGE = "Имя и фамилия могут содержать только буквы, пробел и дефис."
    }
}

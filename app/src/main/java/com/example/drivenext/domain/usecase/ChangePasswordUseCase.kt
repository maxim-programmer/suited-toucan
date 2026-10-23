package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.util.suspendRunCatching
import com.example.drivenext.domain.validation.Validators

class ChangePasswordUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(newPassword: String): Result<Unit> = suspendRunCatching {
        if (!Validators.isPasswordValid(newPassword)) {
            throw AppException(SHORT_PASSWORD_MESSAGE)
        }
        authRepository.changePassword(newPassword)
    }

    private companion object {
        const val SHORT_PASSWORD_MESSAGE = "Пароль должен содержать не менее 6 символов."
    }
}

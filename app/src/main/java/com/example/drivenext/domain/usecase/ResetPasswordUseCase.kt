package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.util.suspendRunCatching

class ResetPasswordUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String): Result<Unit> =
        suspendRunCatching { authRepository.resetPassword(email) }
}

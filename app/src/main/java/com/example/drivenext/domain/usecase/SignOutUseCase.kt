package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.util.suspendRunCatching

class SignOutUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<Unit> =
        suspendRunCatching { authRepository.signOut() }
}

package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.repository.AuthRepository
import com.example.drivenext.domain.util.suspendRunCatching

class SignInUseCase(private val authRepository: AuthRepository) {

    suspend operator fun invoke(email: String, password: String): Result<Unit> =
        suspendRunCatching { authRepository.signIn(email, password) }
}

package com.example.drivenext.domain.usecase

import com.example.drivenext.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class ObserveAuthStateUseCase(private val authRepository: AuthRepository) {

    operator fun invoke(): Flow<Boolean> = authRepository.isAuthenticated
}

package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val isAuthenticated: Flow<Boolean>
    suspend fun hasActiveSession(): Boolean
    suspend fun currentUser(): AuthUser?
    suspend fun signIn(email: String, password: String)
    suspend fun signUp(email: String, password: String)
    suspend fun startGoogleSignIn()
    suspend fun resetPassword(email: String)
    suspend fun changePassword(newPassword: String)
    suspend fun signOut()
}

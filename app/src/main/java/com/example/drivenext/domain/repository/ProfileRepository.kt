package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.AuthUser
import com.example.drivenext.domain.model.RegistrationData
import com.example.drivenext.domain.model.UserProfile

interface ProfileRepository {
    suspend fun saveProfile(userId: String, data: RegistrationData)
    suspend fun loadProfile(user: AuthUser): UserProfile
    suspend fun updateAvatar(userId: String, uri: String): String?
}

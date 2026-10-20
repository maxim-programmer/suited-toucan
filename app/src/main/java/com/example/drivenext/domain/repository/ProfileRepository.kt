package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.RegistrationData

interface ProfileRepository {
    suspend fun saveProfile(userId: String, data: RegistrationData)
}

package com.example.drivenext.domain.model

import java.time.LocalDate

data class RegistrationData(
    val email: String,
    val password: String,
    val lastName: String,
    val firstName: String,
    val middleName: String?,
    val birthDate: LocalDate,
    val gender: Gender,
    val licenseNumber: String,
    val licenseIssueDate: LocalDate,
    val avatarUri: String?,
    val licensePhotoUri: String,
    val passportPhotoUri: String
)

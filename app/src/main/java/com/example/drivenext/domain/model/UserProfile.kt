package com.example.drivenext.domain.model

import java.time.LocalDate

data class UserProfile(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val gender: Gender?,
    val licenseNumber: String?,
    val joinedAt: LocalDate?,
    val avatarUrl: String?,
    val googleEmail: String?
) {
    val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
}

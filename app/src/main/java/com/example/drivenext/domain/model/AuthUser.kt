package com.example.drivenext.domain.model

data class AuthUser(
    val id: String,
    val email: String,
    val hasGoogle: Boolean = false
)

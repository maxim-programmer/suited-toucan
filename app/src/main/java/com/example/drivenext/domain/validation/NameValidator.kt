package com.example.drivenext.domain.validation

object NameValidator {

    const val MAX_NAME_LENGTH = 50

    private val nameRegex = Regex("^[\\p{L}][\\p{L} '\\-]*$")

    fun isNameValid(value: String): Boolean {
        val trimmed = value.trim()
        return trimmed.isNotEmpty() &&
            trimmed.length <= MAX_NAME_LENGTH &&
            nameRegex.matches(trimmed)
    }
}

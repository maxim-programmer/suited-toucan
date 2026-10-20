package com.example.drivenext.presentation.auth.register

import androidx.annotation.StringRes
import com.example.drivenext.domain.model.Gender

data class RegistrationForm(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val agreed: Boolean = false,
    val lastName: String = "",
    val firstName: String = "",
    val middleName: String = "",
    val birthDate: String = "",
    val gender: Gender? = null,
    val licenseNumber: String = "",
    val licenseIssueDate: String = "",
    val avatarUri: String? = null,
    val licensePhotoUri: String? = null,
    val passportPhotoUri: String? = null
) {
    val isStep2Filled: Boolean
        get() = lastName.isNotBlank() &&
            firstName.isNotBlank() &&
            birthDate.isNotBlank() &&
            gender != null

    val isStep3Filled: Boolean
        get() = licenseNumber.isNotBlank() && licenseIssueDate.isNotBlank()
}

data class RegistrationErrors(
    @StringRes val email: Int? = null,
    @StringRes val password: Int? = null,
    @StringRes val repeatPassword: Int? = null,
    @StringRes val agreement: Int? = null,
    @StringRes val requiredFields: Int? = null,
    @StringRes val birthDate: Int? = null,
    @StringRes val licenseNumber: Int? = null,
    @StringRes val issueDate: Int? = null,
    @StringRes val photos: Int? = null
) {
    val hasStep1Errors: Boolean
        get() = email != null || password != null || repeatPassword != null || agreement != null

    val hasStep2Errors: Boolean
        get() = requiredFields != null || birthDate != null

    val hasStep3Errors: Boolean
        get() = requiredFields != null || licenseNumber != null || issueDate != null || photos != null
}

enum class PhotoTarget {
    Avatar,
    License,
    Passport
}

sealed interface RegistrationEvent {
    data object OpenStep2 : RegistrationEvent
    data object OpenStep3 : RegistrationEvent
    data object Succeeded : RegistrationEvent
    data class ShowError(val message: String) : RegistrationEvent
}

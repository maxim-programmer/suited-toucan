package com.example.drivenext.presentation.auth.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.Gender
import com.example.drivenext.domain.model.RegistrationData
import com.example.drivenext.domain.usecase.RegisterUserUseCase
import com.example.drivenext.domain.validation.Validators
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val registerUser: RegisterUserUseCase
) : ViewModel() {

    private val _form = MutableStateFlow(RegistrationForm())
    val form: StateFlow<RegistrationForm> = _form.asStateFlow()

    private val _errors = MutableStateFlow(RegistrationErrors())
    val errors: StateFlow<RegistrationErrors> = _errors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _events = Channel<RegistrationEvent>(Channel.BUFFERED)
    val events: Flow<RegistrationEvent> = _events.receiveAsFlow()

    var pendingPhotoTarget: PhotoTarget? = null
    var pendingCameraUri: String? = null

    fun onEmailChanged(value: String) {
        _form.update { it.copy(email = value) }
        _errors.update { it.copy(email = null) }
    }

    fun onPasswordChanged(value: String) {
        _form.update { it.copy(password = value) }
        _errors.update { it.copy(password = null, repeatPassword = null) }
    }

    fun onRepeatPasswordChanged(value: String) {
        _form.update { it.copy(repeatPassword = value) }
        _errors.update { it.copy(repeatPassword = null) }
    }

    fun onAgreementChanged(value: Boolean) {
        _form.update { it.copy(agreed = value) }
        _errors.update { it.copy(agreement = null) }
    }

    fun onLastNameChanged(value: String) {
        _form.update { it.copy(lastName = value) }
        clearRequiredError()
    }

    fun onFirstNameChanged(value: String) {
        _form.update { it.copy(firstName = value) }
        clearRequiredError()
    }

    fun onMiddleNameChanged(value: String) {
        _form.update { it.copy(middleName = value) }
    }

    fun onBirthDateChanged(value: String) {
        _form.update { it.copy(birthDate = value) }
        _errors.update { it.copy(birthDate = null, requiredFields = null) }
    }

    fun onGenderChanged(value: Gender) {
        _form.update { it.copy(gender = value) }
        clearRequiredError()
    }

    fun onLicenseNumberChanged(value: String) {
        _form.update { it.copy(licenseNumber = value) }
        _errors.update { it.copy(licenseNumber = null, requiredFields = null) }
    }

    fun onLicenseIssueDateChanged(value: String) {
        _form.update { it.copy(licenseIssueDate = value) }
        _errors.update { it.copy(issueDate = null, requiredFields = null) }
    }

    fun onPhotoSelected(target: PhotoTarget, uri: String) {
        _form.update {
            when (target) {
                PhotoTarget.Avatar -> it.copy(avatarUri = uri)
                PhotoTarget.License -> it.copy(licensePhotoUri = uri)
                PhotoTarget.Passport -> it.copy(passportPhotoUri = uri)
            }
        }
        _errors.update { it.copy(photos = null) }
    }

    fun onStep1NextClicked() {
        val form = _form.value
        val errors = RegistrationErrors(
            email = if (Validators.isEmailValid(form.email)) null else R.string.error_invalid_email,
            password = if (Validators.isPasswordValid(form.password)) null else R.string.error_password_short,
            repeatPassword = if (form.password == form.repeatPassword) null else R.string.error_passwords_mismatch,
            agreement = if (form.agreed) null else R.string.error_agreement_required
        )
        _errors.value = errors
        if (errors.hasStep1Errors) return
        _events.trySend(RegistrationEvent.OpenStep2)
    }

    fun onStep2NextClicked() {
        val form = _form.value
        val errors = RegistrationErrors(
            requiredFields = if (form.isStep2Filled) null else R.string.error_required_fields,
            birthDate = if (form.birthDate.isBlank() || Validators.isBirthDateValid(form.birthDate)) {
                null
            } else {
                R.string.error_invalid_birth_date
            }
        )
        _errors.value = errors
        if (errors.hasStep2Errors) return
        _events.trySend(RegistrationEvent.OpenStep3)
    }

    fun onStep3NextClicked() {
        if (_isLoading.value) return
        val form = _form.value
        val errors = RegistrationErrors(
            requiredFields = if (form.isStep3Filled) null else R.string.error_required_fields,
            licenseNumber = if (form.licenseNumber.isBlank() || Validators.isLicenseNumberValid(form.licenseNumber)) {
                null
            } else {
                R.string.error_license_number
            },
            issueDate = if (form.licenseIssueDate.isBlank() || Validators.isIssueDateValid(form.licenseIssueDate)) {
                null
            } else {
                R.string.error_invalid_issue_date
            },
            photos = if (form.licensePhotoUri != null && form.passportPhotoUri != null) {
                null
            } else {
                R.string.error_photos_required
            }
        )
        _errors.value = errors
        if (errors.hasStep3Errors) return

        val data = buildRegistrationData(form) ?: return
        viewModelScope.launch {
            _isLoading.value = true
            val result = registerUser(data)
            _isLoading.value = false
            result
                .onSuccess { _events.send(RegistrationEvent.Succeeded) }
                .onFailure {
                    _events.send(RegistrationEvent.ShowError(it.message ?: AppException.UNKNOWN_MESSAGE))
                }
        }
    }

    private fun clearRequiredError() {
        _errors.update { it.copy(requiredFields = null) }
    }

    private fun buildRegistrationData(form: RegistrationForm): RegistrationData? {
        val birthDate = Validators.parseBirthDate(form.birthDate) ?: return null
        val issueDate = Validators.parseIssueDate(form.licenseIssueDate) ?: return null
        val gender = form.gender ?: return null
        val licensePhoto = form.licensePhotoUri ?: return null
        val passportPhoto = form.passportPhotoUri ?: return null
        return RegistrationData(
            email = form.email.trim(),
            password = form.password,
            lastName = form.lastName.trim(),
            firstName = form.firstName.trim(),
            middleName = form.middleName.trim().ifEmpty { null },
            birthDate = birthDate,
            gender = gender,
            licenseNumber = form.licenseNumber.trim(),
            licenseIssueDate = issueDate,
            avatarUri = form.avatarUri,
            licensePhotoUri = licensePhoto,
            passportPhotoUri = passportPhoto
        )
    }
}

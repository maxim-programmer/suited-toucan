package com.example.drivenext.presentation.addcar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drivenext.R
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.NewCar
import com.example.drivenext.domain.model.Transmission
import com.example.drivenext.domain.usecase.AddCarUseCase
import com.example.drivenext.domain.usecase.UploadCarPhotosUseCase
import com.example.drivenext.domain.validation.CarValidators
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCarViewModel(
    private val addCar: AddCarUseCase,
    private val uploadCarPhotos: UploadCarPhotosUseCase
) : ViewModel() {

    private val _form = MutableStateFlow(AddCarForm())
    val form: StateFlow<AddCarForm> = _form.asStateFlow()

    private val _photos = MutableStateFlow<List<String>>(emptyList())
    val photos: StateFlow<List<String>> = _photos.asStateFlow()

    private val _errors = MutableStateFlow(AddCarErrors())
    val errors: StateFlow<AddCarErrors> = _errors.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _events = Channel<AddCarEvent>(Channel.BUFFERED)
    val events: Flow<AddCarEvent> = _events.receiveAsFlow()

    var pendingCameraUri: String? = null

    private var carId: String? = null

    val remainingPhotoSlots: Int get() = MAX_PHOTOS - _photos.value.size

    fun onAddressChanged(value: String) {
        _form.update { it.copy(address = value) }
    }

    fun onAddressConfirmed() {
        if (_form.value.isAddressFilled) {
            _events.trySend(AddCarEvent.OpenDetails)
        }
    }

    fun onYearChanged(value: String) {
        _form.update { it.copy(year = value) }
        _errors.update { it.copy(year = null) }
    }

    fun onBrandChanged(value: String) {
        _form.update { if (it.brand == value) it else it.copy(brand = value, model = "") }
    }

    fun onModelChanged(value: String) {
        _form.update { it.copy(model = value) }
    }

    fun onTransmissionChanged(value: Transmission?) {
        _form.update { it.copy(transmission = value) }
    }

    fun onMileageChanged(value: String) {
        _form.update { it.copy(mileage = value) }
        _errors.update { it.copy(mileage = null) }
    }

    fun onPricePerDayChanged(value: String) {
        _form.update { it.copy(pricePerDay = value) }
        _errors.update { it.copy(pricePerDay = null) }
    }

    fun onDescriptionChanged(value: String) {
        _form.update { it.copy(description = value) }
    }

    fun onSubmitClicked() {
        val current = _form.value
        if (_isLoading.value || !current.isDetailsFilled) return

        val year = CarValidators.parseYear(current.year)
        val mileage = CarValidators.parseMileage(current.mileage)
        val price = CarValidators.parsePrice(current.pricePerDay)
        _errors.value = AddCarErrors(
            year = if (year == null) R.string.add_car_error_year else null,
            mileage = if (mileage == null) R.string.add_car_error_mileage else null,
            pricePerDay = if (price == null) R.string.add_car_error_price else null
        )
        val transmission = current.transmission
        if (year == null || mileage == null || price == null || transmission == null) return

        val car = NewCar(
            address = current.address.trim(),
            year = year,
            brand = current.brand.trim(),
            model = current.model.trim(),
            transmission = transmission,
            mileage = mileage,
            pricePerDay = price,
            description = current.description.trim()
        )

        viewModelScope.launch {
            _isLoading.value = true
            addCar(carId, car)
                .onSuccess { id ->
                    carId = id
                    _events.send(AddCarEvent.OpenPhotos)
                }
                .onFailure { error ->
                    _events.send(AddCarEvent.ShowError(error.message ?: AppException.UNKNOWN_MESSAGE))
                }
            _isLoading.value = false
        }
    }

    fun onPhotosAdded(uris: List<String>) {
        _photos.update { current -> (current + uris).distinct().take(MAX_PHOTOS) }
    }

    fun onPhotoRemoved(index: Int) {
        _photos.update { current ->
            if (index in current.indices) current.filterIndexed { i, _ -> i != index } else current
        }
    }

    fun onPhotosNextClicked() {
        val id = carId
        val uris = _photos.value
        if (_isLoading.value || id == null || uris.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            uploadCarPhotos(id, uris)
                .onSuccess { _events.send(AddCarEvent.Completed) }
                .onFailure { error ->
                    _events.send(AddCarEvent.ShowError(error.message ?: AppException.UNKNOWN_MESSAGE))
                }
            _isLoading.value = false
        }
    }

    companion object {
        const val MAX_PHOTOS = 5
    }
}
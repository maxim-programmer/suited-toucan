package com.example.drivenext.domain.model

enum class BookingStatus {
    Active,
    Completed,
    Cancelled
}

data class Booking(
    val id: Long,
    val number: Int,
    val carId: String,
    val carBrand: String,
    val carModel: String,
    val carImageUrl: String?,
    val pricePerDay: Int,
    val insurancePerDay: Int,
    val deposit: Int,
    val address: String,
    val startMillis: Long,
    val endMillis: Long,
    val driverName: String,
    val licenseNumber: String,
    val cancelledAtMillis: Long?,
    val status: BookingStatus
) {
    val carTitle: String
        get() = "$carBrand $carModel"

    val cost: RentalCost
        get() = RentalCost.calculate(startMillis, endMillis, pricePerDay, insurancePerDay)
}

data class NewBooking(
    val car: Car,
    val startMillis: Long,
    val endMillis: Long,
    val driverName: String,
    val licenseNumber: String
)

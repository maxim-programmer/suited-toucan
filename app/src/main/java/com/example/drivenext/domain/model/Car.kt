package com.example.drivenext.domain.model

enum class Transmission {
    Automatic,
    Manual
}

enum class FuelType {
    Petrol,
    Diesel,
    Electric,
    Hybrid
}

data class Car(
    val id: String,
    val brand: String,
    val model: String,
    val year: Int,
    val pricePerDay: Int,
    val insurancePerDay: Int,
    val deposit: Int,
    val transmission: Transmission,
    val fuelType: FuelType,
    val address: String,
    val description: String,
    val imageUrl: String?
) {
    val title: String
        get() = if (year > 0) "$brand $model $year" else "$brand $model"
}

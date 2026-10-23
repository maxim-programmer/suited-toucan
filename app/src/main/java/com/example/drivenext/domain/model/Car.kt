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
    val pricePerDay: Int,
    val transmission: Transmission,
    val fuelType: FuelType,
    val imageUrl: String?
)

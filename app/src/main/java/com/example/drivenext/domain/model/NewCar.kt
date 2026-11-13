package com.example.drivenext.domain.model

data class NewCar(
    val address: String,
    val year: Int,
    val brand: String,
    val model: String,
    val transmission: Transmission,
    val mileage: Int,
    val pricePerDay: Int,
    val description: String
)

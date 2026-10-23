package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.Car

interface CarRepository {
    suspend fun getCars(): List<Car>
    suspend fun searchCars(query: String): List<Car>
}

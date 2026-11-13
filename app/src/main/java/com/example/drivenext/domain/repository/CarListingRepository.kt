package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.NewCar

interface CarListingRepository {
    suspend fun saveCar(ownerId: String, carId: String?, car: NewCar): String
    suspend fun uploadPhotos(ownerId: String, carId: String, uris: List<String>)
    suspend fun deleteCar(ownerId: String, carId: String)
}
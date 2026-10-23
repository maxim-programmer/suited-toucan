package com.example.drivenext.domain.repository

interface FavouriteRepository {
    suspend fun getFavouriteIds(userId: String): List<String>
    suspend fun isFavourite(userId: String, carId: String): Boolean
    suspend fun add(userId: String, carId: String)
    suspend fun remove(userId: String, carId: String)
}

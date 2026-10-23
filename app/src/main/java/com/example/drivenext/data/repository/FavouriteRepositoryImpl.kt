package com.example.drivenext.data.repository

import com.example.drivenext.data.local.FavouriteDao
import com.example.drivenext.data.local.FavouriteEntity
import com.example.drivenext.domain.repository.FavouriteRepository

class FavouriteRepositoryImpl(private val favouriteDao: FavouriteDao) : FavouriteRepository {

    override suspend fun getFavouriteIds(userId: String): List<String> =
        favouriteDao.getCarIds(userId)

    override suspend fun isFavourite(userId: String, carId: String): Boolean =
        favouriteDao.count(userId, carId) > 0

    override suspend fun add(userId: String, carId: String) {
        favouriteDao.insert(FavouriteEntity(userId, carId, System.currentTimeMillis()))
    }

    override suspend fun remove(userId: String, carId: String) {
        favouriteDao.delete(userId, carId)
    }
}

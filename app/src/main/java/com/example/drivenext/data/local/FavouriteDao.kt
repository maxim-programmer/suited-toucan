package com.example.drivenext.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavouriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favourite: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE userId = :userId AND carId = :carId")
    suspend fun delete(userId: String, carId: String)

    @Query("SELECT carId FROM favourites WHERE userId = :userId ORDER BY addedAtMillis DESC")
    suspend fun getCarIds(userId: String): List<String>

    @Query("SELECT COUNT(*) FROM favourites WHERE userId = :userId AND carId = :carId")
    suspend fun count(userId: String, carId: String): Int
}

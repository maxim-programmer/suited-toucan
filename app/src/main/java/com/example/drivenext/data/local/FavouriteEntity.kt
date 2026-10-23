package com.example.drivenext.data.local

import androidx.room.Entity

@Entity(tableName = "favourites", primaryKeys = ["userId", "carId"])
data class FavouriteEntity(
    val userId: String,
    val carId: String,
    val addedAtMillis: Long
)

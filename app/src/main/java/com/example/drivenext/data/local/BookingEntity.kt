package com.example.drivenext.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val userId: String,
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
    val createdAtMillis: Long
)

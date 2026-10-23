package com.example.drivenext.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BookingDao {

    @Insert
    suspend fun insert(booking: BookingEntity): Long

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY startMillis DESC")
    suspend fun getAll(userId: String): List<BookingEntity>

    @Query("SELECT * FROM bookings WHERE id = :id AND userId = :userId LIMIT 1")
    suspend fun getById(userId: String, id: Long): BookingEntity?

    @Query(
        "UPDATE bookings SET cancelledAtMillis = :cancelledAtMillis " +
            "WHERE id = :id AND userId = :userId AND cancelledAtMillis IS NULL"
    )
    suspend fun cancel(userId: String, id: Long, cancelledAtMillis: Long): Int
}

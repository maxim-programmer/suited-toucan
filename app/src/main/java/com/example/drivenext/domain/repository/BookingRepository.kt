package com.example.drivenext.domain.repository

import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.NewBooking

interface BookingRepository {
    suspend fun createBooking(userId: String, booking: NewBooking): Long
    suspend fun getBookings(userId: String): List<Booking>
    suspend fun getBooking(userId: String, id: Long): Booking?
    suspend fun cancelBooking(userId: String, id: Long): Boolean
}

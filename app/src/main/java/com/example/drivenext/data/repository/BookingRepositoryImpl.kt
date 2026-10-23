package com.example.drivenext.data.repository

import com.example.drivenext.data.local.BookingDao
import com.example.drivenext.data.local.BookingEntity
import com.example.drivenext.domain.model.Booking
import com.example.drivenext.domain.model.BookingStatus
import com.example.drivenext.domain.model.NewBooking
import com.example.drivenext.domain.repository.BookingRepository
import kotlin.random.Random

class BookingRepositoryImpl(private val bookingDao: BookingDao) : BookingRepository {

    override suspend fun createBooking(userId: String, booking: NewBooking): Long {
        val car = booking.car
        val entity = BookingEntity(
            userId = userId,
            number = Random.nextInt(MIN_NUMBER, MAX_NUMBER),
            carId = car.id,
            carBrand = car.brand,
            carModel = car.model,
            carImageUrl = car.imageUrl,
            pricePerDay = car.pricePerDay,
            insurancePerDay = car.insurancePerDay,
            deposit = car.deposit,
            address = car.address,
            startMillis = booking.startMillis,
            endMillis = booking.endMillis,
            driverName = booking.driverName,
            licenseNumber = booking.licenseNumber,
            cancelledAtMillis = null,
            createdAtMillis = System.currentTimeMillis()
        )
        return bookingDao.insert(entity)
    }

    override suspend fun getBookings(userId: String): List<Booking> {
        val now = System.currentTimeMillis()
        return bookingDao.getAll(userId).map { it.toDomain(now) }
    }

    override suspend fun getBooking(userId: String, id: Long): Booking? =
        bookingDao.getById(userId, id)?.toDomain(System.currentTimeMillis())

    override suspend fun cancelBooking(userId: String, id: Long): Boolean =
        bookingDao.cancel(userId, id, System.currentTimeMillis()) > 0

    private fun BookingEntity.toDomain(now: Long): Booking {
        val status = when {
            cancelledAtMillis != null -> BookingStatus.Cancelled
            endMillis < now -> BookingStatus.Completed
            else -> BookingStatus.Active
        }
        return Booking(
            id = id,
            number = number,
            carId = carId,
            carBrand = carBrand,
            carModel = carModel,
            carImageUrl = carImageUrl,
            pricePerDay = pricePerDay,
            insurancePerDay = insurancePerDay,
            deposit = deposit,
            address = address,
            startMillis = startMillis,
            endMillis = endMillis,
            driverName = driverName,
            licenseNumber = licenseNumber,
            cancelledAtMillis = cancelledAtMillis,
            status = status
        )
    }

    private companion object {
        const val MIN_NUMBER = 100_000
        const val MAX_NUMBER = 1_000_000
    }
}

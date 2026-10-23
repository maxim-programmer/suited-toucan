package com.example.drivenext.data.repository

import com.example.drivenext.data.remote.guardRemote
import com.example.drivenext.data.remote.stringOrNull
import com.example.drivenext.domain.model.Car
import com.example.drivenext.domain.model.FuelType
import com.example.drivenext.domain.model.Transmission
import com.example.drivenext.domain.repository.CarRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class CarRepositoryImpl(private val clientProvider: () -> SupabaseClient) : CarRepository {

    override suspend fun getCars(): List<Car> = guardRemote {
        val client = clientProvider()
        val response = client.postgrest.from(TABLE_NAME).select {
            order("brand", Order.ASCENDING)
            order("model", Order.ASCENDING)
        }
        parseCars(client, response.data)
    }

    override suspend fun searchCars(query: String): List<Car> = guardRemote {
        val client = clientProvider()
        val pattern = "%${sanitize(query)}%"
        val response = client.postgrest.from(TABLE_NAME).select {
            filter {
                or {
                    ilike("brand", pattern)
                    ilike("model", pattern)
                }
            }
            order("brand", Order.ASCENDING)
            order("model", Order.ASCENDING)
        }
        parseCars(client, response.data)
    }

    override suspend fun getCar(id: String): Car? = guardRemote {
        val client = clientProvider()
        val response = client.postgrest.from(TABLE_NAME).select {
            filter { eq("id", id) }
        }
        parseCars(client, response.data).firstOrNull()
    }

    override suspend fun getCarsByIds(ids: List<String>): List<Car> = guardRemote {
        if (ids.isEmpty()) {
            emptyList()
        } else {
            val client = clientProvider()
            val response = client.postgrest.from(TABLE_NAME).select {
                filter { isIn("id", ids) }
            }
            parseCars(client, response.data)
        }
    }

    private fun parseCars(client: SupabaseClient, json: String): List<Car> {
        val bucket = client.storage.from(IMAGES_BUCKET)
        return Json.parseToJsonElement(json).jsonArray.mapNotNull { element ->
            val row = element.jsonObject
            val id = row.stringOrNull("id") ?: return@mapNotNull null
            val brand = row.stringOrNull("brand") ?: return@mapNotNull null
            val model = row.stringOrNull("model") ?: return@mapNotNull null
            Car(
                id = id,
                brand = brand,
                model = model,
                year = row.int("year", 0),
                pricePerDay = row.int("price_per_day", 0),
                insurancePerDay = row.int("insurance_per_day", DEFAULT_INSURANCE),
                deposit = row.int("deposit", DEFAULT_DEPOSIT),
                transmission = parseTransmission(row.stringOrNull("transmission")),
                fuelType = parseFuelType(row.stringOrNull("fuel_type")),
                address = row.stringOrNull("address").orEmpty(),
                description = row.stringOrNull("description").orEmpty(),
                imageUrl = row.stringOrNull("image_path")
                    ?.takeIf { it.isNotBlank() }
                    ?.let { bucket.publicUrl(it) }
            )
        }
    }

    private fun JsonObject.int(key: String, default: Int): Int =
        this[key]?.jsonPrimitive?.doubleOrNull?.toInt() ?: default

    private fun parseTransmission(value: String?): Transmission =
        if (value.equals("manual", ignoreCase = true)) Transmission.Manual else Transmission.Automatic

    private fun parseFuelType(value: String?): FuelType = when (value?.lowercase()) {
        "diesel" -> FuelType.Diesel
        "electric" -> FuelType.Electric
        "hybrid" -> FuelType.Hybrid
        else -> FuelType.Petrol
    }

    private fun sanitize(query: String): String =
        query.trim().filter { it.isLetterOrDigit() || it == ' ' || it == '-' || it == '.' }

    private companion object {
        const val TABLE_NAME = "cars"
        const val IMAGES_BUCKET = "car-images"
        const val DEFAULT_INSURANCE = 300
        const val DEFAULT_DEPOSIT = 15000
    }
}

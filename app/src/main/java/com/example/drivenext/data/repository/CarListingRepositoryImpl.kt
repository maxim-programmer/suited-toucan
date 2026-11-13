package com.example.drivenext.data.repository

import android.content.Context
import android.net.Uri
import com.example.drivenext.data.remote.guardRemote
import com.example.drivenext.data.remote.stringOrNull
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.NewCar
import com.example.drivenext.domain.model.Transmission
import com.example.drivenext.domain.repository.CarListingRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlin.coroutines.cancellation.CancellationException

class CarListingRepositoryImpl(
    context: Context,
    private val clientProvider: () -> SupabaseClient
) : CarListingRepository {

    private val contentResolver = context.applicationContext.contentResolver

    override suspend fun saveCar(ownerId: String, carId: String?, car: NewCar): String = guardRemote {
        val client = clientProvider()
        val row = buildJsonObject {
            put("owner_id", ownerId)
            put("brand", car.brand)
            put("model", car.model)
            put("year", car.year)
            put("transmission", if (car.transmission == Transmission.Manual) "manual" else "automatic")
            put("address", car.address)
            put("description", car.description)
            put("mileage", car.mileage)
            put("price_per_day", car.pricePerDay)
        }
        val table = client.postgrest.from(TABLE_NAME)
        val response = if (carId == null) {
            table.insert(row) { select() }
        } else {
            table.update(row) {
                select()
                filter { eq("id", carId) }
            }
        }
        Json.parseToJsonElement(response.data).jsonArray.firstOrNull()?.jsonObject?.stringOrNull("id")
            ?: throw AppException(CAR_NOT_SAVED_MESSAGE)
    }

    override suspend fun uploadPhotos(ownerId: String, carId: String, uris: List<String>) {
        guardRemote {
            val client = clientProvider()
            val bucket = client.storage.from(IMAGES_BUCKET)
            val paths = uris.mapIndexed { index, uri -> uploadPhoto(bucket, "$ownerId/$carId/$index", uri) }
            val response = client.postgrest.from(TABLE_NAME).update(
                buildJsonObject {
                    put("image_path", paths.first())
                    put("photo_paths", buildJsonArray { paths.forEach { add(it) } })
                }
            ) {
                select()
                filter { eq("id", carId) }
            }
            if (Json.parseToJsonElement(response.data).jsonArray.isEmpty()) {
                throw AppException(CAR_NOT_SAVED_MESSAGE)
            }
        }
    }

    override suspend fun deleteCar(ownerId: String, carId: String) {
        guardRemote {
            val client = clientProvider()
            val response = client.postgrest.from(TABLE_NAME).delete {
                select()
                filter {
                    eq("id", carId)
                    eq("owner_id", ownerId)
                }
            }
            val deleted = Json.parseToJsonElement(response.data).jsonArray.firstOrNull()?.jsonObject
                ?: throw AppException(CAR_NOT_DELETED_MESSAGE)
            val paths = buildList {
                deleted.stringOrNull("image_path")?.takeIf { it.isNotBlank() }?.let { add(it) }
                (deleted["photo_paths"] as? JsonArray)?.forEach { element ->
                    element.jsonPrimitive.contentOrNull?.takeIf { it.isNotBlank() }?.let { add(it) }
                }
            }.distinct()
            if (paths.isNotEmpty()) {
                try {
                    client.storage.from(IMAGES_BUCKET).delete(*paths.toTypedArray())
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Unit
                }
            }
        }
    }

    private suspend fun uploadPhoto(bucket: BucketApi, basePath: String, uriString: String): String {
        val uri = Uri.parse(uriString)
        val mimeType = contentResolver.getType(uri) ?: DEFAULT_MIME_TYPE
        val bytes = try {
            withContext(Dispatchers.IO) {
                contentResolver.openInputStream(uri)?.use { it.readBytes() }
            }
        } catch (e: java.io.IOException) {
            null
        } catch (e: SecurityException) {
            null
        } ?: throw AppException("Не удалось прочитать выбранное фото.")
        if (bytes.size > MAX_PHOTO_SIZE_BYTES) {
            throw AppException("Размер фото не должен превышать 10 МБ.")
        }
        val extension = when (mimeType) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            else -> "jpg"
        }
        val path = "$basePath.$extension"
        bucket.upload(path, bytes) {
            upsert = true
            contentType = ContentType.parse(mimeType)
        }
        return path
    }

    private companion object {
        const val TABLE_NAME = "cars"
        const val IMAGES_BUCKET = "car-images"
        const val DEFAULT_MIME_TYPE = "image/jpeg"
        const val MAX_PHOTO_SIZE_BYTES = 10 * 1024 * 1024
        const val CAR_NOT_SAVED_MESSAGE = "Не удалось сохранить автомобиль. Попробуйте ещё раз."
        const val CAR_NOT_DELETED_MESSAGE = "Не удалось удалить автомобиль. Возможно, он уже удалён."
    }
}
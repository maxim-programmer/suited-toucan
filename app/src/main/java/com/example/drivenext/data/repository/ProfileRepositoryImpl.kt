package com.example.drivenext.data.repository

import android.content.Context
import android.net.Uri
import com.example.drivenext.data.remote.guardRemote
import com.example.drivenext.data.remote.stringOrNull
import com.example.drivenext.domain.model.AppException
import com.example.drivenext.domain.model.AuthUser
import com.example.drivenext.domain.model.Gender
import com.example.drivenext.domain.model.RegistrationData
import com.example.drivenext.domain.model.UserProfile
import com.example.drivenext.domain.repository.ProfileRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.BucketApi
import io.github.jan.supabase.storage.storage
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeParseException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.hours

class ProfileRepositoryImpl(
    context: Context,
    private val clientProvider: () -> SupabaseClient
) : ProfileRepository {

    private val contentResolver = context.applicationContext.contentResolver

    override suspend fun saveProfile(userId: String, data: RegistrationData) {
        guardRemote {
            val client = clientProvider()
            val bucket = client.storage.from(BUCKET_NAME)

            val avatarPath = data.avatarUri?.let { uploadPhoto(bucket, "$userId/avatar", it) }
            val licensePath = uploadPhoto(bucket, "$userId/license", data.licensePhotoUri)
            val passportPath = uploadPhoto(bucket, "$userId/passport", data.passportPhotoUri)

            val row = buildJsonObject {
                put("id", userId)
                put("email", data.email)
                put("last_name", data.lastName)
                put("first_name", data.firstName)
                put("middle_name", data.middleName)
                put("birth_date", data.birthDate.toString())
                put("gender", data.gender.apiValue)
                put("license_number", data.licenseNumber)
                put("license_issue_date", data.licenseIssueDate.toString())
                put("avatar_path", avatarPath)
                put("license_photo_path", licensePath)
                put("passport_photo_path", passportPath)
            }
            client.postgrest.from(TABLE_NAME).upsert(row)
        }
    }

    override suspend fun loadProfile(user: AuthUser): UserProfile = guardRemote {
        val client = clientProvider()
        val response = client.postgrest.from(TABLE_NAME).select {
            filter { eq("id", user.id) }
        }
        val row = Json.parseToJsonElement(response.data).jsonArray.firstOrNull()?.jsonObject
        val avatarPath = row?.stringOrNull("avatar_path")?.takeIf { it.isNotBlank() }
        val genderValue = row?.stringOrNull("gender")
        UserProfile(
            id = user.id,
            email = row?.stringOrNull("email") ?: user.email,
            firstName = row?.stringOrNull("first_name"),
            lastName = row?.stringOrNull("last_name"),
            gender = Gender.entries.firstOrNull { it.apiValue == genderValue },
            joinedAt = row?.stringOrNull("created_at")?.let { parseJoinDate(it) },
            avatarUrl = avatarPath?.let { createSignedUrl(client, it) },
            googleEmail = user.email.takeIf { user.hasGoogle }
        )
    }

    override suspend fun updateAvatar(userId: String, uri: String): String? = guardRemote {
        val client = clientProvider()
        val bucket = client.storage.from(BUCKET_NAME)
        val path = uploadPhoto(bucket, "$userId/avatar", uri)
        client.postgrest.from(TABLE_NAME).update(
            buildJsonObject { put("avatar_path", path) }
        ) {
            filter { eq("id", userId) }
        }
        createSignedUrl(client, path)
    }

    private suspend fun createSignedUrl(client: SupabaseClient, path: String): String? = try {
        client.storage.from(BUCKET_NAME).createSignedUrl(path, SIGNED_URL_LIFETIME)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    private fun parseJoinDate(value: String): LocalDate? = try {
        OffsetDateTime.parse(value).toLocalDate()
    } catch (e: DateTimeParseException) {
        null
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
        const val BUCKET_NAME = "user-documents"
        const val TABLE_NAME = "profiles"
        const val DEFAULT_MIME_TYPE = "image/jpeg"
        const val MAX_PHOTO_SIZE_BYTES = 10 * 1024 * 1024
        val SIGNED_URL_LIFETIME = 1.hours
    }
}

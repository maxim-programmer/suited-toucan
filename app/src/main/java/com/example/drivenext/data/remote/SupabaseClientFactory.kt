package com.example.drivenext.data.remote

import com.example.drivenext.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import kotlin.time.Duration.Companion.seconds

object SupabaseClientFactory {

    const val AUTH_SCHEME = "drivenext"
    const val AUTH_HOST = "login"

    fun create(): SupabaseClient {
        check(BuildConfig.SUPABASE_URL.isNotBlank() && BuildConfig.SUPABASE_ANON_KEY.isNotBlank()) {
            "Не настроено подключение к серверу. Укажите supabase.url и supabase.anon.key в local.properties."
        }
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            requestTimeout = 120.seconds
            install(Auth) {
                scheme = AUTH_SCHEME
                host = AUTH_HOST
            }
            install(Postgrest)
            install(Storage)
        }
    }
}
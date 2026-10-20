package com.example.drivenext.data.remote

import android.content.Intent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.handleDeeplinks

class AuthDeepLinkHandler(private val clientProvider: () -> SupabaseClient) {

    fun handle(intent: Intent?) {
        val received = intent ?: return
        if (received.data == null) return
        try {
            clientProvider().handleDeeplinks(received)
        } catch (e: Exception) {
            return
        }
    }
}

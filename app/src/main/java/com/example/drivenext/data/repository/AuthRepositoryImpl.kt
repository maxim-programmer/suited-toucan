package com.example.drivenext.data.repository

import com.example.drivenext.data.remote.guardRemote
import com.example.drivenext.domain.model.AuthUser
import com.example.drivenext.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl(private val clientProvider: () -> SupabaseClient) : AuthRepository {

    override val isAuthenticated: Flow<Boolean> = flow {
        emitAll(clientProvider().auth.sessionStatus.map { it is SessionStatus.Authenticated })
    }.catch { emit(false) }

    override suspend fun hasActiveSession(): Boolean = try {
        val auth = clientProvider().auth
        withTimeoutOrNull(INITIALIZATION_TIMEOUT_MS) { auth.awaitInitialization() }
        auth.currentSessionOrNull() != null
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        false
    }

    override suspend fun currentUser(): AuthUser? = try {
        clientProvider().auth.currentUserOrNull()?.let { user ->
            AuthUser(
                id = user.id,
                email = user.email.orEmpty(),
                hasGoogle = user.identities.orEmpty().any { it.provider == GOOGLE_PROVIDER }
            )
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        null
    }

    override suspend fun signIn(email: String, password: String) {
        guardRemote {
            clientProvider().auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
        }
    }

    override suspend fun signUp(email: String, password: String) {
        guardRemote {
            val auth = clientProvider().auth
            auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            if (auth.currentSessionOrNull() == null) {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
            }
        }
    }

    override suspend fun startGoogleSignIn() {
        guardRemote { clientProvider().auth.signInWith(Google) }
    }

    override suspend fun resetPassword(email: String) {
        guardRemote { clientProvider().auth.resetPasswordForEmail(email) }
    }

    override suspend fun changePassword(newPassword: String) {
        guardRemote {
            clientProvider().auth.updateUser {
                password = newPassword
            }
        }
    }

    override suspend fun signOut() {
        guardRemote {
            val auth = clientProvider().auth
            try {
                auth.signOut()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                auth.clearSession()
            }
        }
    }

    private companion object {
        const val INITIALIZATION_TIMEOUT_MS = 5000L
        const val GOOGLE_PROVIDER = "google"
    }
}

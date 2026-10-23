package com.example.drivenext.data.remote

import com.example.drivenext.domain.model.AppException
import io.github.jan.supabase.auth.exception.AuthErrorCode
import io.github.jan.supabase.auth.exception.AuthRestException
import io.github.jan.supabase.exceptions.HttpRequestException
import io.github.jan.supabase.exceptions.RestException
import java.io.IOException

private const val NETWORK_MESSAGE =
    "Не удалось связаться с сервером. Проверьте подключение к интернету."

fun Throwable.toAppException(): AppException = when (this) {
    is AppException -> this
    is AuthRestException -> AppException(authMessage(this), this)
    is RestException -> AppException(description ?: error, this)
    is HttpRequestException -> AppException(NETWORK_MESSAGE, this)
    is IOException -> AppException(NETWORK_MESSAGE, this)
    is IllegalStateException -> AppException(message ?: AppException.UNKNOWN_MESSAGE, this)
    else -> AppException(AppException.UNKNOWN_MESSAGE, this)
}

private fun authMessage(exception: AuthRestException): String = when (exception.errorCode) {
    AuthErrorCode.InvalidCredentials -> "Неверная электронная почта или пароль."
    AuthErrorCode.EmailExists,
    AuthErrorCode.UserAlreadyExists -> "Пользователь с такой электронной почтой уже зарегистрирован."
    AuthErrorCode.WeakPassword -> "Пароль слишком простой. Используйте не менее 6 символов."
    AuthErrorCode.EmailNotConfirmed ->
        "Электронная почта не подтверждена. Подтвердите адрес по ссылке из письма."
    AuthErrorCode.OverRequestRateLimit,
    AuthErrorCode.OverEmailSendRateLimit -> "Слишком много попыток. Повторите позже."
    AuthErrorCode.EmailAddressInvalid -> "Введите корректный адрес электронной почты."
    AuthErrorCode.SamePassword -> "Новый пароль должен отличаться от текущего."
    AuthErrorCode.ReauthenticationNeeded -> "Для смены пароля войдите в аккаунт заново."
    AuthErrorCode.SignupDisabled -> "Регистрация временно отключена."
    AuthErrorCode.ProviderDisabled,
    AuthErrorCode.EmailProviderDisabled -> "Выбранный способ входа отключён на сервере."
    else -> exception.errorDescription
}

suspend inline fun <T> guardRemote(block: () -> T): T = try {
    block()
} catch (e: kotlin.coroutines.cancellation.CancellationException) {
    throw e
} catch (e: Throwable) {
    android.util.Log.e("DriveNext", "Remote error: " + e.javaClass.name + ": " + e.message, e)
    throw e.toAppException()
}

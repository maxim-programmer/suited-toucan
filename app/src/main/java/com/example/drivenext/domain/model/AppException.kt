package com.example.drivenext.domain.model

class AppException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    companion object {
        const val UNKNOWN_MESSAGE = "Произошла непредвиденная ошибка. Попробуйте ещё раз."
        const val SESSION_MESSAGE = "Сессия завершена. Войдите в аккаунт снова."
    }
}

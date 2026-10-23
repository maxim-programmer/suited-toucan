package com.example.drivenext.presentation.common

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val RUSSIAN_LOCALE: Locale = Locale.forLanguageTag("ru")
private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm, d MMMM yyyy", RUSSIAN_LOCALE)
private val DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMMM yyyy", RUSSIAN_LOCALE)

fun Long.formatDateTime(): String =
    DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()))

fun Long.formatDate(): String =
    DATE_FORMATTER.format(Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()))

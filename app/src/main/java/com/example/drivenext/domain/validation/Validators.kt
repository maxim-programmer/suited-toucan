package com.example.drivenext.domain.validation

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import java.util.Locale

object Validators {

    const val MIN_PASSWORD_LENGTH = 6
    const val LICENSE_NUMBER_LENGTH = 10
    private const val MIN_YEAR = 1900

    private val emailRegex =
        Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$")

    fun isEmailValid(value: String): Boolean = emailRegex.matches(value.trim())

    fun isPasswordValid(value: String): Boolean = value.length >= MIN_PASSWORD_LENGTH

    fun isLicenseNumberValid(value: String): Boolean = value.trim().length == LICENSE_NUMBER_LENGTH

    fun parseBirthDate(value: String): LocalDate? = parseDate(value, DateFormats.BIRTH_DATE_PATTERN)

    fun parseIssueDate(value: String): LocalDate? = parseDate(value, DateFormats.ISSUE_DATE_PATTERN)

    fun isBirthDateValid(value: String): Boolean = isPastDate(parseBirthDate(value))

    fun isIssueDateValid(value: String): Boolean = isPastDate(parseIssueDate(value))

    private fun isPastDate(date: LocalDate?): Boolean =
        date != null && date.year >= MIN_YEAR && !date.isAfter(LocalDate.now())

    private fun parseDate(value: String, pattern: String): LocalDate? = try {
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT)
        LocalDate.parse(value.trim(), formatter)
    } catch (e: DateTimeParseException) {
        null
    }
}

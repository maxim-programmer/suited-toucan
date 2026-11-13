package com.example.drivenext.domain.validation

import java.time.Year

object CarValidators {

    const val MIN_YEAR = 1950
    const val MAX_MILEAGE = 2_000_000
    const val MAX_PRICE_PER_DAY = 1_000_000

    fun parseYear(value: String): Int? {
        val year = value.trim().toIntOrNull() ?: return null
        return year.takeIf { it in MIN_YEAR..(Year.now().value + 1) }
    }

    fun parsePrice(value: String): Int? {
        val price = value.trim().toIntOrNull() ?: return null
        return price.takeIf { it in 1..MAX_PRICE_PER_DAY }
    }

    fun parseMileage(value: String): Int? {
        val mileage = value.trim().toIntOrNull() ?: return null
        return mileage.takeIf { it in 0..MAX_MILEAGE }
    }
}

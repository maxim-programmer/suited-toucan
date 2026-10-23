package com.example.drivenext.domain.model

data class RentalCost(
    val days: Int,
    val rent: Int,
    val insurance: Int
) {
    val total: Int
        get() = rent + insurance

    companion object {
        private const val DAY_MILLIS = 24L * 60L * 60L * 1000L

        fun calculate(
            startMillis: Long,
            endMillis: Long,
            pricePerDay: Int,
            insurancePerDay: Int
        ): RentalCost {
            val duration = (endMillis - startMillis).coerceAtLeast(0L)
            val days = ((duration + DAY_MILLIS - 1) / DAY_MILLIS).toInt().coerceAtLeast(1)
            return RentalCost(
                days = days,
                rent = days * pricePerDay,
                insurance = days * insurancePerDay
            )
        }
    }
}

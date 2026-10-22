package com.gerwalex.retire

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
expect fun getSystemLocale(): String

expect object CurrencyFormatter {
    val currencySymbol: String
    fun formatCurrency(amount: Float): String
}

expect object NumberFormatter {
    fun formatDecimal(number: Float, decimals: Int = 1, localeTag: String? = null): String
}
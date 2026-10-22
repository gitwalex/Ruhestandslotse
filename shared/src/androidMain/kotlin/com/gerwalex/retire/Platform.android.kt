package com.gerwalex.retire

import android.os.Build
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun getSystemLocale(): String = Locale.getDefault().language

actual object NumberFormatter {

    actual fun formatDecimal(number: Float, decimals: Int, localeTag: String?): String {
        val locale = localeTag?.let { Locale.forLanguageTag(it) } ?: Locale.getDefault()
        val format = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }
        return format.format(number)
    }
}

actual object CurrencyFormatter {

    actual fun formatCurrency(amount: Float): String {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
            currency = Currency.getInstance(Locale.getDefault())
        }
        return format.format(amount)
    }

    actual val currencySymbol: String
        get() = Currency.getInstance(Locale.getDefault()).symbol
}
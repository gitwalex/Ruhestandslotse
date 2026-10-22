package com.gerwalex.retire

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class JVMPlatform: Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()
actual fun getSystemLocale(): String = Locale.getDefault().language

actual val platformModule: Module = module {
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                File(System.getProperty("java.io.tmpdir"), "preferences.preferences_pb").absolutePath.toPath()
            }
        )
    }
}

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

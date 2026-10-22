package com.gerwalex.retire

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale


fun createAndroidDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            context.filesDir.resolve("preferences.preferences_pb").absolutePath.toPath()
        }
    )
}

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

actual val platformModule: Module = module {
    single<DataStore<Preferences>> {
        createAndroidDataStore(androidContext())
    }
}
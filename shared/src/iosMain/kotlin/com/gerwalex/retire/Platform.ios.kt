package com.gerwalex.retire

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.NSNumberFormatterDecimalStyle
import platform.Foundation.NSUserDomainMask
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.localeWithLocaleIdentifier
import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun getSystemLocale(): String {
    return NSLocale.currentLocale.languageCode
}

actual val platformModule: Module = module {
    single<DataStore<Preferences>> {
        createIosDataStore()
    }
}

@OptIn(ExperimentalForeignApi::class)
fun createIosDataStore(): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null
            )
            requireNotNull(documentDirectory).path.let { "$it/preferences.preferences_pb" }.toPath()
        }
    )
}

actual object NumberFormatter {
    actual fun formatDecimal(number: Float, decimals: Int, localeTag: String?): String {
        val formatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            minimumFractionDigits = decimals.toULong()
            maximumFractionDigits = decimals.toULong()
            if (localeTag != null) {
                locale = NSLocale.localeWithLocaleIdentifier(localeTag)
            }
        }
        return formatter.stringFromNumber(NSNumber(number.toDouble())) ?: number.toString()
    }
}

actual object CurrencyFormatter {
    actual fun formatCurrency(amount: Float): String {
        val formatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
        }
        return formatter.stringFromNumber(NSNumber(amount.toDouble())) ?: "$amount"
    }

    actual val currencySymbol: String
        get() {
            val formatter = NSNumberFormatter().apply {
                numberStyle = NSNumberFormatterCurrencyStyle
            }
            return formatter.currencySymbol ?: "€"
        }
}

package com.gerwalex.retire

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun getSystemLocale(): String {
    return NSLocale.currentLocale.languageCode
}
actual object NumberFormatter {
    actual fun formatCurrency(amount: Double, currencyCode: String, localeTag: String?): String {
        val formatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
            this.currencyCode = currencyCode
            if (localeTag != null) {
                locale = NSLocale.localeWithLocaleIdentifier(localeTag)
            }
        }
        return formatter.stringFromNumber(NSNumber(amount)) ?: "$amount $currencyCode"
    }

    actual fun formatDecimal(number: Double, decimals: Int, localeTag: String?): String {
        val formatter = NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterDecimalStyle
            minimumFractionDigits = decimals.toLong()
            maximumFractionDigits = decimals.toLong()
            if (localeTag != null) {
                locale = NSLocale.localeWithLocaleIdentifier(localeTag)
            }
        }
        return formatter.stringFromNumber(NSNumber(number)) ?: number.toString()
    }
}
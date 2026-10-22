package com.gerwalex.retire

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import com.gerwalex.mymonma.R
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import org.jetbrains.compose.resources.stringResource
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.util.Currency

/**
 * Remembers and returns an optimized [com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter] for formatting monetary or numerical
 * values compactly on charts (e.g., axis labels).
 *
 * This function automatically resolves the device's current locale and currency symbol (falling back
 * to "€" if unavailable) and creates a locale-aware [java.text.DecimalFormat] instance.
 *
 * The formatting logic evaluates raw numerical inputs (assumed to be in cents, hence divided by `100.0`
 * to obtain euros) across three distinct magnitude thresholds:
 * 1. **Millions (`>= 1,000,000.0`):** Converts the amount into millions and applies the localized
 *    [com.gerwalex.mymonma.R.string.chart_millions] template string (e.g., "1.0M €" or "1,0 Mio. €").
 * 2. **Thousands (`>= 100,000.0`):** Converts the amount into thousands as an integer and applies
 *    the localized [com.gerwalex.mymonma.R.string.chart_thousands] template string.
 * 3. **Standard Range (`< 100,000.0`):** Formats the amount as a whole number (with 0 fractional digits)
 *    using the locale-specific [java.text.DecimalFormat] and appends the currency symbol.
 *
 * @return A remembered [com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter] configured with compact localized formatting rules.
 */
@Composable
fun rememberCompactCurrencyFormatter(): CartesianValueFormatter {
    val millionsTemplate = stringResource(R.string.chart_millions)
    val thousandsTemplate = stringResource(R.string.chart_thousands)

    val deviceLocale = LocalLocale.current.platformLocale
    val currencySymbol = remember(deviceLocale) {
        try {
            Currency.getInstance(deviceLocale).symbol
        } catch (_: Exception) {
            "€"
        }
    }
    val decimalFormat = remember(deviceLocale) {
        DecimalFormat("#,##0", DecimalFormatSymbols(deviceLocale))
    }

    return remember(millionsTemplate, thousandsTemplate, currencySymbol) {
        CartesianValueFormatter { _, value, _ ->
            val euros = value / 100.0

            when {
                euros >= 1_000_000.0 -> {
                    val millions = euros / 1_000_000.0
                    // Nutzt das template aus strings.xml (z.B. "1.0M €" oder "1,0 Mio. €")
                    String.format(deviceLocale, millionsTemplate, millions, currencySymbol)
                }

                euros >= 100_000.0 -> {
                    val thousands = (euros / 1_000.0).toInt()
                    String.format(deviceLocale, thousandsTemplate, thousands, currencySymbol)
                }

                else -> {
                    val formattedNum =
                        decimalFormat.apply { maximumFractionDigits = 0 }.format(euros)
                    "$formattedNum $currencySymbol"
                }
            }
        }
    }
}

/**
 * Remembers and returns a [CartesianValueFormatter] that converts numerical axis values
 * (such as chart indices or relative year offsets) into absolute calendar year strings.
 *dds this integer value as an offset directly to the current calendar year retrieved via `LocalDate.now().year`.
 *
 * @return A remembered [CartesianValueFormatter] formatting chart axis values as years.
 */
@Composable
fun rememberYearOffsetFormatter(): CartesianValueFormatter {
    return remember {
        CartesianValueFormatter { _, value, _ ->
            "${value.toInt() + LocalDate.now().year}"
        }
    }
}
package com.gerwalex.retire

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import ruhestandslotse.shared.generated.resources.Res
import ruhestandslotse.shared.generated.resources.chart_millions
import ruhestandslotse.shared.generated.resources.chart_thousands
import kotlin.time.Clock

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
fun rememberCompactCurrencyFormatter(currencySymbol: String = "€"): CartesianValueFormatter {
    val millionsTemplate = stringResource(Res.string.chart_millions)
    val thousandsTemplate = stringResource(Res.string.chart_thousands)

    return remember(millionsTemplate, thousandsTemplate, currencySymbol) {
        CartesianValueFormatter { _, value, _ ->
            val euros = value / 100.0

            when {
                euros >= 1_000_000.0 -> {
                    val millions = euros / 1_000_000.0
                    // Einfache plattformunabhängige Formatierung (1 Nachkommastelle)
                    val formattedMillions =
                        ((millions * 10).toInt() / 10.0).toString().replace('.', ',')
                    millionsTemplate
                        .replace("{0}", formattedMillions)
                        .replace("{1}", currencySymbol)
                }

                euros >= 100_000.0 -> {
                    val thousands = (euros / 1_000.0).toInt().toString()
                    thousandsTemplate
                        .replace("{0}", thousands)
                        .replace("{1}", currencySymbol)
                }

                else -> {
                    // Kleinere Beträge direkt als String
                    "${euros.toInt()} $currencySymbol"
                }
            }
        }
    }
}

/**
 * Remembers and returns a [CartesianValueFormatter] that converts numerical axis values
 * into absolute calendar year strings using kotlinx-datetime.
 */
@Composable
fun rememberYearOffsetFormatter(): CartesianValueFormatter {
    val currentYear = remember {
        Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
            .year
    }

    return remember(currentYear) {
        CartesianValueFormatter { _, value, _ ->
            "${value.toInt() + currentYear}"
        }
    }
}
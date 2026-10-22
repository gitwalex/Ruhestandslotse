package com.gerwalex.retire.projection

import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object PreferenceKey {
    val savingsPhaseDuration = intPreferencesKey("savingsPhaseDuration")
    val savingsPhaseAnnualSavings = longPreferencesKey("savingsPhaseAnnualSavings")
    val savingsPhaseDynamizationRate = floatPreferencesKey("savingsPhaseDynamizationRate")
    val savingPhaseReturnRate = floatPreferencesKey("savingPhaseReturnRate")
    val payoutPhaseIncome = longPreferencesKey("payoutPhaseIncome")
    val payoutPhaseReturnRate = floatPreferencesKey("payoutPhaseReturnRate")
    val payoutPhaseRequiredPayoutAmount = longPreferencesKey("payoutPhaseRequiredPayoutAmount")
    val payoutPhaseInflationRate = floatPreferencesKey("payoutPhaseInflationRate")
}
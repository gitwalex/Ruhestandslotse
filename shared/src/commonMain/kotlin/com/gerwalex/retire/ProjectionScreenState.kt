package com.gerwalex.retire

data class ProjectionScreenState(
    val isLoading: Boolean = false,
    val startwert: Long = 0,
    val savingsPhaseDuration: Int = 15,
    val savingsPhaseAnnualSavings: Long = 1000,
    val savingsPhaseDynamizationRate: Float = 0.05f,
    val savingPhaseReturnRate: Float = 0.06f,
    val payoutPhaseRequiredPayoutAmount: Long = 36_000,
    val payoutPhaseIncome: Long = 12_000,
    val payoutPhaseReturnRate: Float = 0.02f,
    val payoutPhaseInflationRate: Float = 0.02f,
    val yearOffsetList: List<Int> = emptyList(),
    val savingPhaseEntries: Map<Int, Long> = buildMap { put(0, 0L) },
    val payoutPhaseEntries: Map<Int, Long> = emptyMap(),
    val currentSavingsMap: Map<Int, Long> = emptyMap(),
    val currentAnnualReturnMap: Map<Int, Long> = emptyMap(),

    )
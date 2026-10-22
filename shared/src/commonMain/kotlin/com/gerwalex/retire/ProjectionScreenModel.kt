package com.gerwalex.retire

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
class ProjectionScreenModel(
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val startwert: Flow<Long> = flow {
        emit(1000000L)
    }

    val preferences = MutableStateFlow(ProjectionScreenState())


    val state: StateFlow<ProjectionScreenState> = combine(startwert, preferences)
    { mw, prefs -> calculatePhasesData(prefs.copy(isLoading = false, startwert = mw)) }
        .flowOn(Dispatchers.Default)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ProjectionScreenState(isLoading = true)
        )

    init {
        with(viewModelScope) {
            launch {
                dataStore.data.first().also { ds ->
                    preferences.update {
                        it.copy(
                            savingsPhaseDuration = ds[PreferenceKey.savingsPhaseDuration]
                                ?: it.savingsPhaseDuration,
                            savingsPhaseAnnualSavings = ds[PreferenceKey.savingsPhaseAnnualSavings]
                                ?: it.savingsPhaseAnnualSavings,
                            savingsPhaseDynamizationRate = ds[PreferenceKey.savingsPhaseDynamizationRate]
                                ?: it.savingsPhaseDynamizationRate,
                            savingPhaseReturnRate = ds[PreferenceKey.savingPhaseReturnRate]
                                ?: it.savingPhaseReturnRate,

                            payoutPhaseInflationRate = ds[PreferenceKey.payoutPhaseInflationRate]
                                ?: it.payoutPhaseInflationRate,
                            payoutPhaseIncome = ds[PreferenceKey.payoutPhaseIncome]
                                ?: it.payoutPhaseIncome,
                            payoutPhaseRequiredPayoutAmount = ds[PreferenceKey.payoutPhaseRequiredPayoutAmount]
                                ?: it.payoutPhaseRequiredPayoutAmount,
                            payoutPhaseReturnRate = ds[PreferenceKey.savingPhaseReturnRate]
                                ?: it.payoutPhaseReturnRate,
                        )
                    }
                }
                preferences
                    .debounce(300.milliseconds)
                    .distinctUntilChanged()
                    .collect { state ->
                        dataStore.edit {
                            it[PreferenceKey.savingsPhaseDuration] =
                                state.savingsPhaseDuration
                            it[PreferenceKey.savingPhaseReturnRate] =
                                state.savingPhaseReturnRate
                            it[PreferenceKey.savingsPhaseAnnualSavings] =
                                state.savingsPhaseAnnualSavings
                            it[PreferenceKey.savingsPhaseDynamizationRate] =
                                state.savingsPhaseDynamizationRate

                            it[PreferenceKey.payoutPhaseIncome] =
                                state.payoutPhaseIncome
                            it[PreferenceKey.payoutPhaseInflationRate] =
                                state.payoutPhaseInflationRate
                            it[PreferenceKey.payoutPhaseReturnRate] =
                                state.payoutPhaseReturnRate
                            it[PreferenceKey.payoutPhaseRequiredPayoutAmount] =
                                state.payoutPhaseRequiredPayoutAmount

                        }
                    }
            }
        }
    }

    fun updateSavingsPhaseDuration(value: Int) {
        viewModelScope.launch {
            preferences.update { it.copy(savingsPhaseDuration = value) }
        }
    }

    fun updateSavingPhaseReturnRate(value: Float) {
        viewModelScope.launch {
            preferences.update {
                val stepSize = 0.01f
                val roundedValue = (value / stepSize) * stepSize
                it.copy(savingPhaseReturnRate = roundedValue)
            }
        }
    }

    fun updateSavingPhaseAnnualSavings(value: Long) {
        viewModelScope.launch {
            val stepSize = 500L
            val roundedValue = (value / stepSize) * stepSize
            preferences.update { it.copy(savingsPhaseAnnualSavings = roundedValue) }
        }
    }

    fun updateSavingPhaseDynamizationRate(value: Float) {
        viewModelScope.launch {
            preferences.update {
                val stepSize = 0.01f
                val roundedValue = (value / stepSize) * stepSize
                it.copy(savingsPhaseDynamizationRate = roundedValue)
            }
        }
    }


    fun updatePayoutPhaseIncome(value: Long) {
        preferences.update {
            val stepSize = 500L
            val roundedValue = (value / stepSize) * stepSize
            it.copy(payoutPhaseIncome = roundedValue)
        }
    }


    fun updatePayoutInflationRate(value: Float) {
        viewModelScope.launch {
            preferences.update {
                val stepSize = 0.01f
                val roundedValue = (value / stepSize) * stepSize
                it.copy(payoutPhaseInflationRate = roundedValue)
            }
        }
    }

    fun updatePayoutPhaseReturnRate(value: Float) {
        viewModelScope.launch {
            preferences.update {
                val stepSize = 0.01f
                val roundedValue = (value / stepSize) * stepSize
                it.copy(payoutPhaseReturnRate = roundedValue)
            }
        }
    }


    fun updateRequiredPayoutAmount(value: Long) {
        viewModelScope.launch {
            preferences.update {
                val stepSize = 500L
                val roundedValue = (value / stepSize) * stepSize
                it.copy(payoutPhaseRequiredPayoutAmount = roundedValue)
            }
        }
    }


    /**
     * Calculates and updates the chart projection data for both the savings phase and payout phase
     * based on the provided [ProjectionScreenState].
     *
     * Computations are performed on [Dispatchers.Default] due to CPU-intensive looping and financial projections.
     *
     * The projection consists of two main phases:
     * 1. **Savings Phase (`for` loop):** Iterates year-by-year through the savings phase duration.
     *    In each year, annual savings are dynamized using the dynamization rate, investment returns are
     *    calculated using the savings phase return rate, and both are added to the accumulated market value.
     * 2. **Payout Phase (`while` loop):** Iterates year-by-year starting after the savings phase until the
     *    projected market value is depleted (`projectionMartkwert > 0`). In each year, required payout amounts
     *    are adjusted for inflation, annual returns and income are added, and the payout is subtracted.
     *    The safety guardrail (`if (index > 50) break`) ensures that the loop terminates if the portfolio does not
     *    deplete within 50 iterations, preventing potential infinite loops.
     *
     * Finally, the resulting series (`xValues` and `yValues`) are pushed to the [ProjectionScreenState] to update the chart.
     *
     * @param state The current [ProjectionScreenState] containing financial parameters such as initial
     *              market value, savings duration, return rates, inflation, and income/payout amounts.
     */
    suspend fun calculatePhasesData(state: ProjectionScreenState): ProjectionScreenState {
        return withContext(Dispatchers.Default) {
            var projectionMartkwert: Long = state.startwert
            var currentSavings = state.savingsPhaseAnnualSavings * 100L
            val currentSavingsMap = mutableMapOf(0 to currentSavings)
            val currentAnnualReturnMap = mutableMapOf(0 to 0L)
            val savingPhaseEntries = buildMap {
                put(0, projectionMartkwert)
                for (i in 1..state.savingsPhaseDuration) {
                    currentSavings =
                        (currentSavings * (1 + state.savingsPhaseDynamizationRate)).toLong()
                    val currentAnnualReturn =
                        (projectionMartkwert * state.savingPhaseReturnRate).toLong()
                    currentSavingsMap[i] = currentSavings
                    currentAnnualReturnMap[i] = currentAnnualReturn
                    projectionMartkwert += currentSavings + currentAnnualReturn
                    put(i, projectionMartkwert)
                }
            }
            var index = state.savingsPhaseDuration
            var currentPayout = state.payoutPhaseRequiredPayoutAmount * 100
            var currentIncome = state.payoutPhaseIncome * 100
            val payoutPhaseEntries = buildMap {
                put(index++, projectionMartkwert)
                while (projectionMartkwert > 0) {
                    currentPayout = (currentPayout * (1 + state.payoutPhaseInflationRate)).toLong()
                    val currentAnnualReturn =
                        (projectionMartkwert * state.payoutPhaseReturnRate).toLong()
                    projectionMartkwert =
                        projectionMartkwert + currentAnnualReturn + currentIncome - currentPayout
                    put(index++, projectionMartkwert)
                    if (index > 50) break
                }
            }
            state.copy(
                savingPhaseEntries = savingPhaseEntries,
                payoutPhaseEntries = payoutPhaseEntries,
                currentSavingsMap = currentSavingsMap,
                currentAnnualReturnMap = currentAnnualReturnMap
            )
        }
    }
}

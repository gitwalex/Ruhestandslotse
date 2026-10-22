package com.gerwalex.retire.projection

class ProjectionScreenActions(
    val updateSavingsPhaseDuration: (Int) -> Unit = {},
    val updateSavingPhaseAnnualSavings: (Long) -> Unit = {},
    val updateSavingPhaseDynamizationRate: (Float) -> Unit = {},
    val updateRequiredPayoutAmount: (Long) -> Unit = {},
    val updateSavingPhaseReturnRate: (Float) -> Unit = {},
    val updatePayoutPhaseIncome: (Long) -> Unit = {},
    val updatePayoutInflationRate: (Float) -> Unit = {},
    val updatePayoutPhaseReturnRate: (Float) -> Unit = {},
)
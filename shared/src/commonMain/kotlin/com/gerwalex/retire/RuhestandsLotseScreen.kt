package com.gerwalex.retire

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun ProjectionScreen() {
    Scaffold {
        ProjectionScreen(
            modifier = Modifier
                .padding(it)
        )
    }
}

@Composable
fun ProjectionScreen(modifier: Modifier = Modifier) {
    val model: ProjectionScreenModel = koinViewModel()
    val state by model.state.collectAsStateWithLifecycle()
    val actions = ProjectionScreenActions(
        updateSavingsPhaseDuration = model::updateSavingsPhaseDuration,
        updateSavingPhaseReturnRate = model::updateSavingPhaseReturnRate,
        updateSavingPhaseAnnualSavings = model::updateSavingPhaseAnnualSavings,
        updateSavingPhaseDynamizationRate = model::updateSavingPhaseDynamizationRate,
        updateRequiredPayoutAmount = model::updateRequiredPayoutAmount,
        updatePayoutPhaseIncome = model::updatePayoutPhaseIncome,
        updatePayoutInflationRate = model::updatePayoutInflationRate,
        updatePayoutPhaseReturnRate = model::updatePayoutPhaseReturnRate
    )
    ProjectionScreen(
        state = state,
        actions = actions,
        modifier = modifier
    )
}

@Composable
fun ProjectionScreen(
    state: ProjectionScreenState,
    actions: ProjectionScreenActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProjectionScreenChart(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        )
        ProjectionPhaseContent(
            state = state,
            actions = actions,
            modifier = Modifier
                .weight(1f)
                .widthIn(max = 600.dp)
        )
    }
}

@Preview(widthDp = 1400, heightDp = 800)
@Preview(device = "spec:width=500dp,height=800dp")
@Composable
private fun ProjectionScreenPreview() {
    ProjectionScreen(
        state = ProjectionScreenState(
            startwert = 100_000_00L,
            payoutPhaseReturnRate = 0.05f,
            savingsPhaseDuration = 10,
            savingsPhaseAnnualSavings = 10_000L,
            payoutPhaseInflationRate = 0.02f,
            savingsPhaseDynamizationRate = 0.01f,
            payoutPhaseRequiredPayoutAmount = 24_000L,
            payoutPhaseIncome = 0L
        ),
        actions = ProjectionScreenActions(),
        modifier = Modifier.fillMaxWidth()
    )
}

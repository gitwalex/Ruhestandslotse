package com.gerwalex.retire

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gerwalex.mymonma.database.room.MyConverter
import java.text.NumberFormat

@Composable
fun ProjectionPhaseContent(
    state: ProjectionScreenState,
    actions: ProjectionScreenActions,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf("Sparphase", "Auszahlungsphase")
    Column(modifier = modifier) {
        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(text = title) }
                )
            }
        }
        // Je nach ausgewähltem Tab die entsprechende Card anzeigen
        Box(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedTabIndex) {
                0 -> SavingPhaseActionCard(state, actions, modifier = Modifier.fillMaxWidth())
                1 -> PayoutPhaseActionCard(state, actions, modifier = Modifier.fillMaxWidth())
            }
        }
    }

}

@Composable
fun SavingPhaseActionCard(
    state: ProjectionScreenState,
    actions: ProjectionScreenActions,
    modifier: Modifier = Modifier,
    maxFractionDigits: Int = 1,
) {
    val sliderModifier = Modifier
        .padding(top = 4.dp)
        .border(Dp.Hairline, color = MaterialTheme.colorScheme.onSurface)
    val formatter = remember(maxFractionDigits) {
        NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = maxFractionDigits
            minimumFractionDigits = maxFractionDigits // Keine Nullen auffüllen
        }
    }
    Card(modifier = modifier.padding(vertical = 8.dp)) {
        FormattedMaterial3Slider(
            modifier = sliderModifier,
            value = state.savingsPhaseDuration.toFloat(),
            onValueChange = {
                actions.updateSavingsPhaseDuration(it.toInt())
            },
            valueRange = 0f..45f,
            valueFormatter = { "${it.toInt()} Jahre" },
            header = { Text(text = "Dauer Sparphase") }
        )
        FormattedMaterial3Slider(
            modifier = sliderModifier,
            value = state.savingsPhaseAnnualSavings.toFloat(),
            onValueChange = {
                actions.updateSavingPhaseAnnualSavings(it.toLong())
            },
            valueRange = 0f..24_000f,
            valueFormatter = { MyConverter.currencyFormatter.getFormattedValue(it * 100) },
            header = { Text(text = "Jährliche Sparrate") }
        )
        FormattedMaterial3Slider(
            modifier = sliderModifier,
            value = state.savingsPhaseDynamizationRate * 1000f,
            onValueChange = {
                actions.updateSavingPhaseDynamizationRate(it.toInt() / 1000f)
            },
            valueRange = 0f..150f,
            valueFormatter = { "${formatter.format(it / 10f)}%" },
            header = { Text(text = "Dynamisierungsrate") }
        )
        FormattedMaterial3Slider(
            modifier = sliderModifier,
            value = state.savingPhaseReturnRate * 1000f,
            onValueChange = {
                actions.updateSavingPhaseReturnRate(it.toInt() / 1000f)
            },
            valueRange = 0f..80f,
            valueFormatter = { "${formatter.format(it / 10f)}%" },
            header = { Text(text = "Erwartete Rendite") }
        )
    }
}

@Composable
fun PayoutPhaseActionCard(
    state: ProjectionScreenState,
    actions: ProjectionScreenActions,
    modifier: Modifier = Modifier,
    maxFractionDigits: Int = 1,
) {
    val formatter = remember(maxFractionDigits) {
        NumberFormat.getNumberInstance().apply {
            maximumFractionDigits = maxFractionDigits
            minimumFractionDigits = maxFractionDigits // Keine Nullen auffüllen
        }
    }
    Card(modifier = modifier.padding(vertical = 8.dp)) {
        FormattedMaterial3Slider(
            value = state.payoutPhaseRequiredPayoutAmount.toFloat(),
            onValueChange = {
                actions.updateRequiredPayoutAmount(it.toLong())
            },
            valueRange = 0f..60_000f,

            valueFormatter = { MyConverter.currencyFormatter.getFormattedValue(it * 100) },
            header = { Text(text = "Jährliche Auszahlung") }
        )
        FormattedMaterial3Slider(
            value = state.payoutPhaseIncome.toFloat(),
            onValueChange = {
                actions.updatePayoutPhaseIncome(it.toLong())
            },
            valueRange = 0f..24_000f,

            valueFormatter = { MyConverter.currencyFormatter.getFormattedValue(it * 100) },
            header = { Text(text = "Einkommen") }
        )
        FormattedMaterial3Slider(
            value = state.payoutPhaseReturnRate * 1000f,
            onValueChange = { actions.updatePayoutPhaseReturnRate(it.toInt() / 1000f) },
            valueRange = 0f..60f,
            valueFormatter = { "${formatter.format(it / 10f)}%" },
            header = { Text(text = "Rendite") }
        )
        FormattedMaterial3Slider(
            value = state.payoutPhaseInflationRate * 1000f,
            onValueChange = {
                actions.updatePayoutInflationRate(it.toInt() / 1000f)
            },
            valueRange = 0f..50f,
            valueFormatter = { "${formatter.format(it / 10f)}%" },
            header = { Text(text = "Inflationsrate") })

    }
}
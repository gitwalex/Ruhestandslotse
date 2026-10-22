package com.gerwalex.retire.projection

import androidx.compose.animation.core.spring
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gerwalex.retire.CurrencyFormatter
import com.gerwalex.retire.rememberCompactCurrencyFormatter
import com.gerwalex.retire.rememberYearOffsetFormatter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.lineModel
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent


@Composable
fun ProjectionScreenChart(
    state: ProjectionScreenState,
    modifier: Modifier = Modifier,
) {
    val zoomState = rememberVicoZoomState(initialZoom = Zoom.Content)
    val modelProducer = remember { CartesianChartModelProducer() }
    val valueMarker = rememberDefaultCartesianMarker(
        label = rememberTextComponent(),
        valueFormatter = { _, _ ->
            CurrencyFormatter.formatCurrency(
                (state.payoutPhaseEntries.values.firstOrNull() ?: 0L).toFloat()
            )
        },
        guideline = rememberLineComponent(
            fill = Fill(MaterialTheme.colorScheme.onSurface),
            thickness = 1.5.dp,
        )
    )
    LaunchedEffect(state.savingPhaseEntries, state.payoutPhaseEntries) {
        modelProducer.runTransaction {
            lineModel {
                series(state.savingPhaseEntries.keys, state.savingPhaseEntries.values)
                if (state.payoutPhaseEntries.isNotEmpty()) {
                    series(state.payoutPhaseEntries.keys, state.payoutPhaseEntries.values)
                }
            }
        }

    }
    Box(modifier = modifier) {
        CartesianChartHost(
            zoomState = zoomState,
            chartAreaHeight = 200.dp,
            animationSpec = null, // Deaktiviert Animationen bei späteren Daten-Updates
            initialAnimationSpec = spring(),
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider =
                        LineCartesianLayer.LineProvider.series(
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(Color.Blue)),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    Fill(Color.Blue.copy(alpha = 0.3f))
                                )
                            ),
                            LineCartesianLayer.Line(
                                fill = LineCartesianLayer.LineFill.single(Fill(Color(0xFFFF9800))),
                                areaFill = LineCartesianLayer.AreaFill.single(
                                    Fill(Color(0xFFFF9800).copy(alpha = 0.3f))
                                )
                            )
                        ),
                ),
                startAxis = VerticalAxis.rememberStart(valueFormatter = rememberCompactCurrencyFormatter()),
                bottomAxis = HorizontalAxis.rememberBottom(valueFormatter = rememberYearOffsetFormatter()),
//                marker = marker,
                persistentMarkers = {
                    valueMarker at state.savingsPhaseDuration
                },
            ),
            modelProducer = modelProducer,


            )
    }
}

@Preview
@Composable
fun ProjectionScreenChartPreview() {


    val state = ProjectionScreenState(
        savingPhaseEntries = listOf(1, 2, 3, 4).zip(listOf(2L, 4, 3, 5)).toMap(),
        payoutPhaseEntries = listOf(1, 2, 3, 4).zip(listOf(3L, 7, 5, 9)).toMap(),
    )


    ProjectionScreenChart(
        state = state,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.Black)
    )
}
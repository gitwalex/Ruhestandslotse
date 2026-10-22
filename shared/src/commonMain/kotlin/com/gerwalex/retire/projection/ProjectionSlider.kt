package com.gerwalex.retire.projection

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormattedMaterial3Slider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    steps: Int = 0,
    valueFormatter: (Float) -> String = { it.toInt().toString() },
    isDynamic: Boolean = true,
    header: @Composable () -> Unit,
) {
    var sliderValue by remember { mutableFloatStateOf(value) }
    var maxLimit by remember { mutableFloatStateOf(valueRange.endInclusive) }

    val sliderState = remember(maxLimit) {
        SliderState(
            value = sliderValue.coerceIn(valueRange.start, maxLimit),
            valueRange = 0f..maxLimit,
            steps = steps
        )
    }
    val activeTrackColor = MaterialTheme.colorScheme.primary
    LaunchedEffect(value) {
        if (sliderState.value != value) {
            sliderState.value = value
        }
    }
    LaunchedEffect(sliderState.value) {
        onValueChange(sliderState.value)
    }
    if (isDynamic) {
        LaunchedEffect(sliderState) {
            snapshotFlow { sliderState.isDragging }
                .collect { isPressed ->
                    // Wenn isPressed von true auf false springt, wurde das Draggen beendet
                    if (!isPressed) {
                        val diff = ((valueRange.endInclusive - valueRange.start) * 0.2).toFloat()

                        if (sliderState.value >= maxLimit * 0.8f) {
                            sliderValue = sliderState.value
                            maxLimit += diff
                        } else if (maxLimit > valueRange.endInclusive && sliderState.value <= maxLimit * 0.2f) {
                            sliderValue = sliderState.value
                            maxLimit = (maxLimit - diff).coerceAtLeast(valueRange.endInclusive)
                        }
                    }
                }
        }
    }
    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            header()
            Slider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                state = sliderState,
                track = { state ->
                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp) // Höhe der Spur
                    ) {
                        val fraction = state.value.let {
                            (it - state.valueRange.start) / (state.valueRange.endInclusive - state.valueRange.start)
                        }
                        val thumbPosition = size.width * fraction

                        // Inaktiver Track (Gesamte Breite)
                        drawLine(
                            color = Color.LightGray,
                            start = Offset(0f, center.y),
                            end = Offset(size.width, center.y),
                            strokeWidth = size.height,
                            cap = StrokeCap.Round
                        )

                        // Aktiver Track (Von Anfang bis zum Daumen)
                        drawLine(
                            color = activeTrackColor,
                            start = Offset(0f, center.y),
                            end = Offset(thumbPosition, center.y),
                            strokeWidth = size.height,
                            cap = StrokeCap.Round
                        )
                    }
                },
                thumb = { state ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.offset(y = (-16).dp) // Feinejustierung des Abstands zur Spur
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = valueFormatter(state.value),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }

                        // 2. Der kleine Dreiecks-Pfeil
                        val primaryColor = MaterialTheme.colorScheme.primary
                        Canvas(
                            modifier = Modifier.size(width = 10.dp, height = 6.dp)
                        ) {
                            val path = Path().apply {
                                moveTo(0f, 0f)
                                lineTo(size.width, 0f)
                                lineTo(size.width / 2f, size.height)
                                close()
                            }
                            drawPath(path = path, color = primaryColor)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )

                    }
                })

            // Min- und Max-Beschriftungen an den Enden
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = valueFormatter(sliderState.valueRange.start),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = valueFormatter(sliderState.valueRange.endInclusive),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

package com.kiras.statistics.presentation.dashboard.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import kotlin.math.max
import kotlin.math.min

@Composable
fun WeekBarChart(
    barFractions: List<Float>,
    modifier: Modifier = Modifier
) {
    val gridColor = MaterialTheme.colorScheme.surfaceVariant
    val barColor = MaterialTheme.colorScheme.primary
    val stubColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val progress = rememberChartEntryProgress()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        for(line in 0..3) {
            val y = size.height * line / 3
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val slotWidth = size.width / barFractions.size
        val barWidth = slotWidth * 2 / 3
        val stubHeight = 4.dp.toPx()
        barFractions.forEachIndexed { index, fraction ->
            val left = slotWidth * index + (slotWidth - barWidth) / 2
            if(fraction <= 0f) {
                drawRoundRect(
                    color = stubColor,
                    topLeft = Offset(left, size.height - stubHeight),
                    size = Size(barWidth, stubHeight),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            } else {
                val barHeight = max(size.height * fraction, stubHeight) * progress.value
                drawRoundRect(
                    color = barColor,
                    topLeft = Offset(left, size.height - barHeight),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(min(8.dp.toPx(), barHeight / 2))
                )
            }
        }
    }
}

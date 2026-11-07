package com.kiras.statistics.presentation.dashboard.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp

@Composable
fun PaceLineChart(
    pointFractions: List<Float?>,
    modifier: Modifier = Modifier
) {
    val gridColor = MaterialTheme.colorScheme.surfaceVariant
    val lineColor = MaterialTheme.colorScheme.secondary
    val dotRingColor = MaterialTheme.colorScheme.surface
    val progress = rememberChartEntryProgress()

    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
            .drawWithCache {
                val startX = 10.dp.toPx()
                val stepX = (size.width - 16.dp.toPx() - startX) / (pointFractions.size - 1)
                val points = pointFractions.mapIndexedNotNull { index, fraction ->
                    fraction?.let {
                        Offset(
                            x = startX + stepX * index,
                            y = size.height * (0.75f - it * 0.48f)
                        )
                    }
                }
                val linePath = Path().apply {
                    points.forEachIndexed { index, point ->
                        if(index == 0) moveTo(point.x, point.y) else lineTo(point.x, point.y)
                    }
                }
                val fillPath = Path().apply {
                    addPath(linePath)
                    points.lastOrNull()?.let { lineTo(it.x, size.height) }
                    points.firstOrNull()?.let { lineTo(it.x, size.height) }
                    close()
                }
                val lineStroke = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
                val dotRadius = 5.dp.toPx()
                val dotRing = Stroke(width = 2.dp.toPx())

                onDrawBehind {
                    listOf(1f / 6f, 3f / 6f, 5f / 6f).forEach { line ->
                        val y = size.height * line
                        drawLine(
                            color = gridColor,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    clipRect(right = size.width * progress.value) {
                        if(points.size > 1) {
                            drawPath(path = fillPath, color = lineColor.copy(alpha = 0.12f))
                            drawPath(path = linePath, color = lineColor, style = lineStroke)
                        }
                        points.lastOrNull()?.let { latest ->
                            drawCircle(color = lineColor, radius = dotRadius, center = latest)
                            drawCircle(
                                color = dotRingColor,
                                radius = dotRadius,
                                center = latest,
                                style = dotRing
                            )
                        }
                    }
                }
            }
    )
}

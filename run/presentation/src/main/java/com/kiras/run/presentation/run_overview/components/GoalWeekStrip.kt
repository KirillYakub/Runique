package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kiras.run.domain.goal.DayGoalStatus
import com.kiras.run.presentation.R
import com.kiras.run.presentation.util.toDescriptionRes
import com.kiras.run.presentation.util.toFullName
import com.kiras.run.presentation.util.toNarrowName
import java.time.DayOfWeek

@Composable
fun GoalWeekStrip(
    week: List<DayGoalStatus>,
    todayIndex: Int,
    modifier: Modifier = Modifier
) {
    val dayDescriptions = DayOfWeek.entries.zip(week) { day, status ->
        stringResource(status.toDescriptionRes(), day.toFullName())
    }
    val weekDescription = stringResource(
        R.string.week_progress,
        dayDescriptions.joinToString(separator = "; ")
    )
    Column(
        modifier = modifier.clearAndSetSemantics {
            contentDescription = weekDescription
        },
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            week.forEach { status ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DayStatusIndicator(status = status)
                }
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEachIndexed { index, day ->
                val isToday = index == todayIndex
                Text(
                    text = day.toNarrowName(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if(isToday) FontWeight.Bold else FontWeight.Normal,
                    color = if(isToday) MaterialTheme.colorScheme.onPrimaryContainer
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DayStatusIndicator(status: DayGoalStatus) {
    val primary = MaterialTheme.colorScheme.primary
    val accent = MaterialTheme.colorScheme.onPrimaryContainer
    val outline = MaterialTheme.colorScheme.outline
    Canvas(modifier = Modifier.size(20.dp)) {
        val strokeWidth = 1.5.dp.toPx()
        val ringRadius = size.minDimension / 2 - strokeWidth / 2
        when(status) {
            DayGoalStatus.MET -> drawCircle(color = primary)
            DayGoalStatus.TODAY -> {
                drawCircle(color = primary.copy(alpha = 0.15f), radius = ringRadius)
                drawCircle(color = accent, radius = ringRadius, style = Stroke(strokeWidth))
            }
            DayGoalStatus.MISSED,
            DayGoalStatus.UPCOMING -> drawCircle(
                color = outline,
                radius = ringRadius,
                style = Stroke(strokeWidth)
            )
            DayGoalStatus.REST -> drawCircle(color = outline, radius = 2.dp.toPx())
        }
    }
}

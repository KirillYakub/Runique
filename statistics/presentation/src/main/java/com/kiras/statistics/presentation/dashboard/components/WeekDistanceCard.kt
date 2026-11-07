package com.kiras.statistics.presentation.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kiras.statistics.presentation.R
import com.kiras.statistics.presentation.dashboard.model.WeekActivityUi
import com.kiras.statistics.presentation.util.toFormattedPercentChange
import com.kiras.statistics.presentation.util.toFullLabel
import com.kiras.statistics.presentation.util.toNarrowLabel
import java.time.DayOfWeek

@Composable
fun WeekDistanceCard(
    week: WeekActivityUi,
    modifier: Modifier = Modifier
) {
    StatisticsCardSurface(
        verticalSpacing = 12.dp,
        modifier = modifier
    ) {
        StatisticsSectionHeader(
            label = stringResource(R.string.this_week),
            stat = week.total,
            trailing = {
                week.changePercent?.let { percent ->
                    WeekChangePill(percent = percent)
                }
            }
        )

        val daySummary = DayOfWeek.entries
            .zip(week.dayDistances) { day, distance -> "${day.toFullLabel()} $distance" }
            .joinToString()
        val chartDescription = stringResource(R.string.week_chart_description, daySummary)
        WeekBarChart(
            barFractions = week.barFractions,
            modifier = Modifier.semantics { contentDescription = chartDescription }
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            DayOfWeek.entries.forEachIndexed { index, day ->
                val isToday = index == week.todayIndex
                Text(
                    text = day.toNarrowLabel(),
                    color = if(isToday) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if(isToday) FontWeight.Bold else FontWeight.Normal
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WeekChangePill(percent: Int) {
    val isPositive = percent > 0
    Text(
        text = stringResource(R.string.week_change, percent.toFormattedPercentChange()),
        color = if(isPositive) {
            MaterialTheme.colorScheme.onPrimaryContainer
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        modifier = Modifier
            .clip(CircleShape)
            .background(
                if(isPositive) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

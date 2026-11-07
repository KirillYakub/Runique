package com.kiras.statistics.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kiras.statistics.presentation.R
import com.kiras.statistics.presentation.dashboard.model.PaceTrendUi
import com.kiras.statistics.presentation.dashboard.model.TrendDirection

@Composable
fun PaceTrendCard(
    trend: PaceTrendUi,
    title: String,
    isSpeedShown: Boolean,
    modifier: Modifier = Modifier
) {
    StatisticsCardSurface(
        verticalSpacing = 12.dp,
        modifier = modifier
    ) {
        StatisticsSectionHeader(
            label = title,
            stat = trend.headline,
            trailing = {
                Text(
                    text = stringResource(R.string.last_8_weeks),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        )

        val chartSummary = trend.rangeText ?: "${trend.headline.value} ${trend.headline.unit}"
        val chartDescription = stringResource(R.string.trend_chart_description, title, chartSummary)
        PaceLineChart(
            pointFractions = trend.pointFractions,
            modifier = if(trend.hasData) {
                Modifier.semantics { contentDescription = chartDescription }
            } else {
                Modifier
            }
        )

        if(!trend.hasData) {
            Text(
                text = stringResource(R.string.trend_no_runs),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        trend.rangeText?.let { rangeText ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = rangeText,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = trend.toChangeText(isSpeedShown),
                    color = if(trend.direction == TrendDirection.IMPROVED) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun PaceTrendUi.toChangeText(isSpeedShown: Boolean): String {
    val amount = changeAmount.orEmpty()
    return when {
        direction == TrendDirection.UNCHANGED -> stringResource(R.string.trend_no_change)
        isSpeedShown -> amount
        direction == TrendDirection.IMPROVED -> stringResource(R.string.trend_faster, amount)
        else -> stringResource(R.string.trend_slower, amount)
    }
}

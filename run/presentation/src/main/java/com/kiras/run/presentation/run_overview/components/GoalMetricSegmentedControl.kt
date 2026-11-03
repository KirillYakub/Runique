package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.util.toLabelRes

@Composable
fun GoalMetricSegmentedControl(
    selectedMetric: GoalMetric,
    onMetricChange: (GoalMetric) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GoalMetric.entries.forEach { metric ->
            val isSelected = metric == selectedMetric
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if(isSelected) MaterialTheme.colorScheme.primary
                        else Color.Transparent
                    )
                    .selectable(
                        selected = isSelected,
                        role = Role.Tab,
                        onClick = { onMetricChange(metric) }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(metric.toLabelRes()),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if(isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

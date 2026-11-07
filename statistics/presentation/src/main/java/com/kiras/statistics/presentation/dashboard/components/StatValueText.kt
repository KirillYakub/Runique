package com.kiras.statistics.presentation.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.kiras.statistics.presentation.dashboard.model.StatValueUi

@Composable
fun StatValueText(
    stat: StatValueUi,
    valueStyle: TextStyle,
    unitStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stat.value,
            color = MaterialTheme.colorScheme.onSurface,
            style = valueStyle,
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text = stat.unit,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = unitStyle,
            modifier = Modifier.alignByBaseline()
        )
    }
}

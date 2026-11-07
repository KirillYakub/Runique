package com.kiras.run.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.presentation.designsystem.SplitlineTheme

@Composable
fun SplitRow(
    label: String,
    barFraction: Float,
    valueText: String,
    isFastest: Boolean,
    isPartial: Boolean,
    trackColor: Color,
    paceFormat: PaceFormat,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.widthIn(min = 28.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(10.dp)
                .clip(CircleShape)
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(barFraction)
                    .clip(CircleShape)
                    .background(
                        when {
                            isFastest -> MaterialTheme.colorScheme.primary
                            isPartial -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                            else -> MaterialTheme.colorScheme.secondary
                        }
                    )
            )
        }
        Text(
            text = valueText,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if(isFastest) FontWeight.Bold else FontWeight.Normal,
            color = if(isFastest) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.End,
            maxLines = 1,
            modifier = Modifier.widthIn(
                min = when(paceFormat) {
                    PaceFormat.PACE -> 44.dp
                    PaceFormat.SPEED -> 76.dp
                }
            )
        )
    }
}

@Preview
@Composable
private fun SplitRowPreview() {
    SplitlineTheme {
        SplitRow(
            label = "3",
            barFraction = 1f,
            valueText = "5:08",
            isFastest = true,
            isPartial = false,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            paceFormat = PaceFormat.PACE
        )
    }
}

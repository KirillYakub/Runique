package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.run.presentation.R
import com.kiras.run.presentation.components.SplitRow
import com.kiras.run.presentation.run_overview.model.RunSplitRowUi
import com.kiras.run.presentation.run_overview.model.RunSplitsUi

@Composable
fun RunSplitsPanel(
    splits: RunSplitsUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.splits),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(splits.labelRes),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        splits.rows.forEach { row ->
            SplitRow(
                label = row.label,
                barFraction = row.barFraction,
                valueText = row.valueText,
                isFastest = row.isFastest,
                isPartial = row.isPartial,
                trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                paceFormat = splits.paceFormat
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.splits_summary_average, splits.averageText),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if(splits.fastestNumber != null && splits.fastestValueText != null) {
                Text(
                    text = stringResource(
                        R.string.splits_summary_fastest,
                        stringResource(splits.unitLabelRes),
                        splits.fastestNumber,
                        splits.fastestValueText
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Preview
@Composable
private fun RunSplitsPanelPreview() {
    SplitlineTheme {
        RunSplitsPanel(
            splits = RunSplitsUi(
                rows = listOf(
                    RunSplitRowUi("1", "5:34", 0.78f, isFastest = false, isPartial = false),
                    RunSplitRowUi("2", "5:02", 1f, isFastest = true, isPartial = false),
                    RunSplitRowUi("0.42", "2:16", 0.88f, isFastest = false, isPartial = true)
                ),
                labelRes = R.string.splits_label_min_per_km,
                unitLabelRes = R.string.unit_km,
                paceFormat = PaceFormat.PACE,
                averageText = "5:21 / km",
                fastestNumber = 2,
                fastestValueText = "5:02"
            )
        )
    }
}

package com.kiras.run.presentation.active_run.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.ui.formatted
import com.kiras.core.presentation.ui.toFormattedPaceOrSpeed
import com.kiras.core.presentation.ui.toFormattedSpeed
import com.kiras.run.presentation.R
import com.kiras.run.presentation.active_run.model.RunSplitUi
import com.kiras.run.presentation.util.toDistanceValueText
import com.kiras.run.presentation.util.toUnitLabelRes
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Composable
fun ActiveRunSheetContent(
    distanceMeters: Int,
    elapsedTime: Duration,
    maxSpeedKmh: Double,
    distanceUnit: DistanceUnit,
    paceFormat: PaceFormat,
    isRunning: Boolean,
    splits: List<RunSplitUi>,
    isSplitsExpanded: Boolean,
    onSplitsToggle: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onPeekContentHeightChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .onSizeChanged { onPeekContentHeightChange(it.height) }
            .navigationBarsPadding()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.distance).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = distanceMeters.toDistanceValueText(distanceUnit),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alignByBaseline()
                )
                Text(
                    text = stringResource(distanceUnit.toUnitLabelRes()),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.alignByBaseline()
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            RunStatTile(
                label = stringResource(R.string.time),
                value = elapsedTime.formatted(),
                modifier = Modifier.weight(1f)
            )
            RunStatTile(
                label = stringResource(
                    when(paceFormat) {
                        PaceFormat.PACE -> R.string.pace
                        PaceFormat.SPEED -> R.string.speed
                    }
                ),
                value = elapsedTime.toFormattedPaceOrSpeed(
                    distanceMeters = distanceMeters,
                    unit = distanceUnit,
                    paceFormat = paceFormat
                ),
                modifier = Modifier.weight(1f)
            )
            RunStatTile(
                label = stringResource(R.string.max_speed),
                value = maxSpeedKmh.toFormattedSpeed(distanceUnit),
                modifier = Modifier.weight(1f)
            )
        }
        SplitsCard(
            splits = splits,
            isExpanded = isSplitsExpanded,
            distanceUnit = distanceUnit,
            paceFormat = paceFormat,
            onToggle = onSplitsToggle
        )
        RunControlButton(
            isRunning = isRunning,
            onStartClick = onStartClick,
            onStopClick = onStopClick
        )
    }
}

@Preview
@Composable
private fun ActiveRunSheetContentPreview() {
    SplitlineTheme {
        ActiveRunSheetContent(
            distanceMeters = 3860,
            elapsedTime = 20.minutes,
            maxSpeedKmh = 14.2,
            distanceUnit = DistanceUnit.KILOMETERS,
            paceFormat = PaceFormat.PACE,
            isRunning = true,
            splits = listOf(
                RunSplitUi(1, "5:34", 0.79f, false, Location(50.1, 14.4)),
                RunSplitUi(2, "5:26", 0.85f, false, Location(50.11, 14.41)),
                RunSplitUi(3, "5:08", 1f, true, Location(50.12, 14.42))
            ),
            isSplitsExpanded = true,
            onSplitsToggle = {},
            onStartClick = {},
            onStopClick = {},
            onPeekContentHeightChange = {}
        )
    }
}

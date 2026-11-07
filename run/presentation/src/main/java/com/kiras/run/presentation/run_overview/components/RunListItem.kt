package com.kiras.run.presentation.run_overview.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.run.RecordedSplit
import com.kiras.core.domain.run.Run
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.UserSettings
import com.kiras.core.presentation.designsystem.RunNavigationIcon
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.run.presentation.R
import com.kiras.run.presentation.run_overview.mappers.toRunUi
import com.kiras.run.presentation.run_overview.model.RunDataUi
import com.kiras.run.presentation.run_overview.model.RunSplitsUi
import com.kiras.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

@Composable
fun RunListItem(
    runUi: RunUi,
    isSplitsExpanded: Boolean,
    onSplitsToggle: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(20.dp)
    var showDropDown by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.surfaceContainerHighest, shape)
                .combinedClickable(
                    onClick = {},
                    onLongClickLabel = stringResource(R.string.delete_run_hint),
                    onLongClick = { showDropDown = true }
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            MapImage(
                imageUrl = runUi.mapPictureUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            )
            RunCardHeader(runUi = runUi)
            RunCardStatRow(
                stats = listOf(
                    RunDataUi(stringResource(R.string.time), runUi.shortDuration),
                    RunDataUi(stringResource(R.string.distance), runUi.preciseDistance),
                    RunDataUi(stringResource(R.string.pace), runUi.averagePace)
                )
            )
            RunCardStatRow(
                stats = listOf(
                    RunDataUi(stringResource(R.string.avg_speed), runUi.avgSpeed),
                    RunDataUi(stringResource(R.string.max_speed), runUi.maxSpeed),
                    RunDataUi(
                        stringResource(R.string.elevation),
                        stringResource(R.string.elevation_gain_value, runUi.totalElevation)
                    )
                )
            )
            if(runUi.splits != null) {
                RunCardSplits(
                    splits = runUi.splits,
                    isExpanded = isSplitsExpanded,
                    onToggle = onSplitsToggle
                )
            }
        }
        DropdownMenu(
            expanded = showDropDown,
            onDismissRequest = { showDropDown = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.delete))
                },
                onClick = {
                    showDropDown = false
                    onDeleteClick()
                }
            )
        }
    }
}

@Composable
private fun RunCardHeader(
    runUi: RunUi,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(shape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.35f),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = RunNavigationIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(20.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = runUi.dateTitle,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(
                    R.string.run_start_and_total_time,
                    runUi.startTime,
                    runUi.shortDuration
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun RunCardStatRow(
    stats: List<RunDataUi>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stats.forEach { stat ->
            RunCardStat(
                stat = stat,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun RunCardStat(
    stat: RunDataUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 10.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {},
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stat.name.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = stat.value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2
        )
    }
}

@Composable
private fun RunCardSplits(
    splits: RunSplitsUi,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AnimatedContent(
            targetState = isExpanded,
            transitionSpec = {
                fadeIn() togetherWith fadeOut() using SizeTransform(clip = true)
            },
            contentAlignment = Alignment.TopCenter,
            label = "run_splits_panel",
            modifier = Modifier.fillMaxWidth()
        ) { expanded ->
            if(expanded) {
                RunSplitsPanel(
                    splits = splits,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            } else {
                Spacer(modifier = Modifier.fillMaxWidth())
            }
        }
        RunSplitsButton(
            isExpanded = isExpanded,
            onClick = onToggle
        )
    }
}

@Preview
@Composable
private fun RunListItemPreview() {
    SplitlineTheme {
        RunListItem(
            runUi = Run(
                id = "123",
                duration = 10.minutes,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 1420,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 12.543,
                totalElevationMeters = 120,
                mapPictureUrl = null,
                splits = listOf(
                    RecordedSplit(DistanceUnit.KILOMETERS, 1, 1000.0, 7.minutes),
                    RecordedSplit(DistanceUnit.KILOMETERS, 2, 420.0, 3.minutes)
                )
            ).toRunUi(UserSettings()),
            isSplitsExpanded = true,
            onSplitsToggle = {},
            onDeleteClick = {}
        )
    }
}

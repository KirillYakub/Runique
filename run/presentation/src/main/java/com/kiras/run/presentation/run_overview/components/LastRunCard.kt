package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.run.Run
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.run.presentation.R
import com.kiras.run.presentation.run_overview.mappers.toRunUi
import com.kiras.run.presentation.run_overview.model.RunDataUi
import com.kiras.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

@Composable
fun LastRunCard(
    runUi: RunUi,
    date: String,
    onSeeAllRunsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.last_run),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        MapImage(
            imageUrl = runUi.mapPictureUrl,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(
                RunDataUi(
                    name = stringResource(R.string.distance),
                    value = runUi.distance
                ),
                RunDataUi(
                    name = stringResource(R.string.pace),
                    value = runUi.pace
                ),
                RunDataUi(
                    name = stringResource(R.string.duration),
                    value = runUi.duration
                )
            ).forEach { runDataUi ->
                LastRunStat(
                    runDataUi = runDataUi,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Text(
            text = stringResource(R.string.see_all_runs),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable(onClick = onSeeAllRunsClick)
        )
    }
}

@Composable
private fun LastRunStat(
    runDataUi: RunDataUi,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = runDataUi.value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = runDataUi.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
private fun LastRunCardPreview() {
    SplitlineTheme {
        LastRunCard(
            runUi = Run(
                id = "1",
                duration = 45.minutes,
                dateTimeUtc = ZonedDateTime.now(),
                distanceMeters = 8420,
                location = Location(0.0, 0.0),
                maxSpeedKmh = 14.2,
                totalElevationMeters = 56,
                mapPictureUrl = null
            ).toRunUi(),
            date = "Yesterday · 07:12",
            onSeeAllRunsClick = {}
        )
    }
}

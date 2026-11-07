package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.run.Run
import com.kiras.core.domain.settings.UserSettings
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineScaffold
import com.kiras.run.presentation.run_overview.mappers.toRunUi
import com.kiras.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

@Composable
fun AllRunsPage(
    runs: List<RunUi>,
    monthRunCount: Int,
    monthDistance: String,
    expandedRunIds: Set<String>,
    onBackClick: () -> Unit,
    onSplitsToggle: (runId: String) -> Unit,
    onDeleteRunClick: (RunUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val layoutDirection = LocalLayoutDirection.current
    SplitlineScaffold(
        modifier = modifier,
        topAppBar = {
            AllRunsTopBar(
                monthRunCount = monthRunCount,
                monthDistance = monthDistance,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(
                start = padding.calculateStartPadding(layoutDirection),
                top = padding.calculateTopPadding(),
                end = padding.calculateEndPadding(layoutDirection),
                bottom = padding.calculateBottomPadding() + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = runs,
                key = { it.id }
            ) {
                RunListItem(
                    runUi = it,
                    isSplitsExpanded = it.id in expandedRunIds,
                    onSplitsToggle = { onSplitsToggle(it.id) },
                    onDeleteClick = { onDeleteRunClick(it) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview
@Composable
private fun AllRunsPagePreview() {
    SplitlineTheme {
        AllRunsPage(
            runs = listOf(
                Run(
                    id = "123",
                    duration = 10.minutes,
                    dateTimeUtc = ZonedDateTime.now(),
                    distanceMeters = 1000,
                    location = Location(0.0, 0.0),
                    maxSpeedKmh = 12.543,
                    totalElevationMeters = 120,
                    mapPictureUrl = null
                ).toRunUi(UserSettings())
            ),
            monthRunCount = 1,
            monthDistance = "1.0 km",
            expandedRunIds = emptySet(),
            onBackClick = {},
            onSplitsToggle = {},
            onDeleteRunClick = {}
        )
    }
}

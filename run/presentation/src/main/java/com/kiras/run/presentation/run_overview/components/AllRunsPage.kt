package com.kiras.run.presentation.run_overview.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.run.Run
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineScaffold
import com.kiras.core.presentation.designsystem.components.SplitlineToolbar
import com.kiras.run.presentation.R
import com.kiras.run.presentation.run_overview.mappers.toRunUi
import com.kiras.run.presentation.run_overview.model.RunUi
import java.time.ZonedDateTime
import kotlin.time.Duration.Companion.minutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRunsPage(
    runs: List<RunUi>,
    onBackClick: () -> Unit,
    onDeleteRunClick: (RunUi) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    SplitlineScaffold(
        modifier = modifier,
        topAppBar = {
            SplitlineToolbar(
                showBackButton = true,
                title = stringResource(R.string.all_runs),
                onBackClick = onBackClick,
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(horizontal = 16.dp),
            contentPadding = padding,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = runs,
                key = { it.id }
            ) {
                RunListItem(
                    runUi = it,
                    onDeleteClick = { onDeleteRunClick(it) }
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
                ).toRunUi()
            ),
            onBackClick = {},
            onDeleteRunClick = {}
        )
    }
}

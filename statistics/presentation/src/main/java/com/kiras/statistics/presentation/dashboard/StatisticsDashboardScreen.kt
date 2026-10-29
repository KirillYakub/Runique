package com.kiras.statistics.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineBottomBar
import com.kiras.core.presentation.designsystem.components.SplitlineScaffold
import com.kiras.core.presentation.designsystem.components.SplitlineToolbar
import com.kiras.core.presentation.designsystem.components.util.BottomBarTab
import com.kiras.statistics.presentation.R
import com.kiras.statistics.presentation.dashboard.components.StatisticsCard
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatisticsDashboardScreenRoot(
    onHomeClick: () -> Unit,
    viewModel: StatisticsDashboardViewModel = koinViewModel()
) {
    StatisticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                StatisticsDashboardAction.OnHomeClick -> onHomeClick()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StatisticsDashboardScreen(
    state: StatisticsDashboardState?,
    onAction: (StatisticsDashboardAction) -> Unit
) {
    SplitlineScaffold(
        topAppBar = {
            SplitlineToolbar(
                showBackButton = false,
                title = stringResource(id = R.string.statistics)
            )
        },
        bottomBar = {
            SplitlineBottomBar(
                selectedTab = BottomBarTab.STATISTICS,
                onTabClick = { tab ->
                    if(tab == BottomBarTab.HOME) {
                        onAction(StatisticsDashboardAction.OnHomeClick)
                    }
                }
            )
        }
    ) { padding ->
        if(state == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    StatisticsCard(
                        title = stringResource(R.string.total_distance_run),
                        value = state.totalDistanceRun,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatisticsCard(
                        title = stringResource(R.string.total_time_run),
                        value = state.totalTimeRun,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    StatisticsCard(
                        title = stringResource(R.string.fastest_ever_run),
                        value = state.fastestEverRun,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    StatisticsCard(
                        title = stringResource(R.string.average_distance_per_run),
                        value = state.avgDistance,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    StatisticsCard(
                        title = stringResource(R.string.average_pace),
                        value = state.avgPace,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun StatisticsDashboardScreenPreview() {
    SplitlineTheme {
        StatisticsDashboardScreen(
            state = StatisticsDashboardState(
                totalDistanceRun = "1000 km",
                totalTimeRun = "0d 0h 0m",
                fastestEverRun = "143.9 km/h",
                avgDistance = "0.1 km",
                avgPace = "07:10"
            ),
            onAction = {}
        )
    }
}

package com.kiras.statistics.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineBottomBar
import com.kiras.core.presentation.designsystem.components.SplitlineScaffold
import com.kiras.core.presentation.designsystem.components.util.BottomBarTab
import com.kiras.statistics.presentation.R
import com.kiras.statistics.presentation.dashboard.components.PaceTrendCard
import com.kiras.statistics.presentation.dashboard.components.StatisticsCard
import com.kiras.statistics.presentation.dashboard.components.WeekDistanceCard
import com.kiras.statistics.presentation.dashboard.model.PaceTrendUi
import com.kiras.statistics.presentation.dashboard.model.StatValueUi
import com.kiras.statistics.presentation.dashboard.model.TrendDirection
import com.kiras.statistics.presentation.dashboard.model.WeekActivityUi
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StatisticsDashboardScreenRoot(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: StatisticsDashboardViewModel = koinViewModel()
) {
    StatisticsDashboardScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                StatisticsDashboardAction.OnHomeClick -> onHomeClick()
                StatisticsDashboardAction.OnSettingsClick -> onSettingsClick()
            }
        }
    )
}

@Composable
private fun StatisticsDashboardScreen(
    state: StatisticsDashboardState,
    onAction: (StatisticsDashboardAction) -> Unit
) {
    SplitlineScaffold(
        bottomBar = {
            SplitlineBottomBar(
                selectedTab = BottomBarTab.STATISTICS,
                onTabClick = { tab ->
                    when(tab) {
                        BottomBarTab.HOME -> onAction(StatisticsDashboardAction.OnHomeClick)
                        BottomBarTab.SETTINGS -> onAction(StatisticsDashboardAction.OnSettingsClick)
                        BottomBarTab.STATISTICS -> Unit
                    }
                }
            )
        }
    ) { padding ->
        if(state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 4.dp)) {
                    Text(
                        text = stringResource(R.string.statistics),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.headlineLarge
                    )
                    Text(
                        text = pluralStringResource(
                            R.plurals.statistics_subtitle,
                            state.runCount,
                            state.runCount
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticsCard(
                        icon = ImageVector.vectorResource(R.drawable.stats_route),
                        label = stringResource(R.string.total_distance_run),
                        stat = state.totalDistance,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    StatisticsCard(
                        icon = ImageVector.vectorResource(R.drawable.stats_clock),
                        label = stringResource(R.string.total_time_run),
                        stat = state.totalTime,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatisticsCard(
                        icon = ImageVector.vectorResource(R.drawable.stats_bolt),
                        label = stringResource(R.string.fastest_ever_run),
                        stat = state.fastestRun,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                    StatisticsCard(
                        icon = ImageVector.vectorResource(R.drawable.stats_ruler),
                        label = stringResource(R.string.average_distance_per_run),
                        stat = state.avgDistance,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }
                val paceTitle = stringResource(
                    if(state.isSpeedShown) R.string.average_speed else R.string.average_pace
                )
                StatisticsCard(
                    icon = ImageVector.vectorResource(R.drawable.stats_pace),
                    label = paceTitle,
                    stat = state.avgPaceOrSpeed,
                    modifier = Modifier.fillMaxWidth()
                )
                WeekDistanceCard(
                    week = state.weekActivity,
                    modifier = Modifier.fillMaxWidth()
                )
                PaceTrendCard(
                    trend = state.paceTrend,
                    title = paceTitle,
                    isSpeedShown = state.isSpeedShown,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

private fun previewState() = StatisticsDashboardState(
    isLoading = false,
    runCount = 12,
    totalDistance = StatValueUi("86.4", "km"),
    totalTime = StatValueUi("7 h 52", "min"),
    fastestRun = StatValueUi("14.8", "km/h"),
    avgDistance = StatValueUi("7.2", "km"),
    avgPaceOrSpeed = StatValueUi("5:28", "/km"),
    weekActivity = WeekActivityUi(
        total = StatValueUi("19.8", "km"),
        changePercent = 14,
        barFractions = listOf(0.556f, 0.933f, 0f, 0.356f, 0f, 0.356f, 0f),
        dayDistances = listOf("5.0 km", "8.4 km", "0.0 km", "3.2 km", "0.0 km", "3.2 km", "0.0 km"),
        todayIndex = 3
    ),
    paceTrend = PaceTrendUi(
        headline = StatValueUi("5:24", "/km"),
        pointFractions = listOf(0f, 0.13f, 0.043f, 0.391f, 0.478f, 0.696f, 0.783f, 1f),
        rangeText = "6:02 → 5:24 /km",
        changeAmount = "38 s",
        direction = TrendDirection.IMPROVED
    )
)

@Preview(heightDp = 1036)
@Composable
private fun StatisticsDashboardScreenDayPreview() {
    SplitlineTheme(isDarkTheme = false) {
        StatisticsDashboardScreen(state = previewState(), onAction = {})
    }
}

@Preview(heightDp = 1036)
@Composable
private fun StatisticsDashboardScreenNightPreview() {
    SplitlineTheme(isDarkTheme = true) {
        StatisticsDashboardScreen(state = previewState(), onAction = {})
    }
}

@Preview(heightDp = 1036)
@Composable
private fun StatisticsDashboardScreenNoRunsPreview() {
    SplitlineTheme(isDarkTheme = false) {
        StatisticsDashboardScreen(
            state = StatisticsDashboardState(
                isLoading = false,
                totalDistance = StatValueUi("0.0", "km"),
                totalTime = StatValueUi("0", "min"),
                fastestRun = StatValueUi("0.0", "km/h"),
                avgDistance = StatValueUi("0.0", "km"),
                avgPaceOrSpeed = StatValueUi("-", "/km"),
                weekActivity = WeekActivityUi(
                    total = StatValueUi("0.0", "km"),
                    todayIndex = 3
                ),
                paceTrend = PaceTrendUi(headline = StatValueUi("-", "/km"))
            ),
            onAction = {}
        )
    }
}

@Preview(heightDp = 1036)
@Composable
private fun StatisticsDashboardScreenSpeedMilesPreview() {
    SplitlineTheme(isDarkTheme = false) {
        StatisticsDashboardScreen(
            state = previewState().copy(
                totalDistance = StatValueUi("53.7", "mi"),
                fastestRun = StatValueUi("9.2", "mph"),
                avgDistance = StatValueUi("4.5", "mi"),
                avgPaceOrSpeed = StatValueUi("6.8", "mph"),
                isSpeedShown = true,
                weekActivity = previewState().weekActivity.copy(
                    total = StatValueUi("12.3", "mi"),
                    changePercent = -6
                ),
                paceTrend = previewState().paceTrend.copy(
                    headline = StatValueUi("6.9", "mph"),
                    rangeText = "6.2 → 6.9 mph",
                    changeAmount = "+0.7 mph"
                )
            ),
            onAction = {}
        )
    }
}

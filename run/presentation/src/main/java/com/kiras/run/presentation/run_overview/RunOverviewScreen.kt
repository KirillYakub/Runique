package com.kiras.run.presentation.run_overview

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineBottomBar
import com.kiras.core.presentation.designsystem.components.SplitlineScaffold
import com.kiras.core.presentation.designsystem.components.util.BottomBarTab
import com.kiras.core.presentation.ui.ObserveAsEvents
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.run.domain.goal.DailyGoalProgress
import com.kiras.run.domain.goal.DayGoalStatus
import com.kiras.run.domain.goal.GoalMetric
import com.kiras.run.presentation.R
import com.kiras.run.presentation.run_overview.components.AllRunsPage
import com.kiras.run.presentation.run_overview.components.DailyGoalBottomSheet
import com.kiras.run.presentation.run_overview.components.DailyGoalCard
import com.kiras.run.presentation.run_overview.components.HomeHeader
import com.kiras.run.presentation.run_overview.components.LastRunCard
import com.kiras.run.presentation.run_overview.components.StartRunButton
import com.kiras.run.presentation.run_overview.mappers.toDailyGoalUi
import com.kiras.run.presentation.util.toGreetingRes
import com.kiras.run.presentation.util.toHomeHeaderDate
import com.kiras.run.presentation.util.toLastRunDate
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate
import java.time.ZonedDateTime

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    val context = LocalContext.current
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is RunOverviewEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_LONG).show()
            }
        }
    }
    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                RunOverviewAction.OnStartClick -> onStartRunClick()
                RunOverviewAction.OnLogOutClick -> onLogoutClick()
                RunOverviewAction.OnStatisticsClick -> onStatisticsClick()
                RunOverviewAction.OnSettingsClick -> onSettingsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun RunOverviewScreen(
    state: RunOverviewState,
    onAction: (RunOverviewAction) -> Unit
) {
    val now = remember { ZonedDateTime.now() }
    BackHandler(enabled = state.isAllRunsOpen) {
        onAction(RunOverviewAction.OnAllRunsBackClick)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SplitlineScaffold(
            floatingActionButton = {
                StartRunButton(
                    onClick = { onAction(RunOverviewAction.OnStartClick) }
                )
            },
            bottomBar = {
                SplitlineBottomBar(
                    selectedTab = BottomBarTab.HOME,
                    onTabClick = { tab ->
                        when(tab) {
                            BottomBarTab.STATISTICS -> onAction(RunOverviewAction.OnStatisticsClick)
                            BottomBarTab.SETTINGS -> onAction(RunOverviewAction.OnSettingsClick)
                            BottomBarTab.HOME -> Unit
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = padding,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    HomeHeader(
                        date = now.toHomeHeaderDate(),
                        greeting = stringResource(now.toGreetingRes(), state.username),
                        username = state.username,
                        isAccountMenuOpen = state.isAccountMenuOpen,
                        onAccountClick = { onAction(RunOverviewAction.OnAccountClick) },
                        onAccountMenuDismiss = { onAction(RunOverviewAction.OnAccountMenuDismiss) },
                        onLogoutClick = { onAction(RunOverviewAction.OnLogOutClick) },
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }
                if(state.isDailyGoalLoaded) {
                    item {
                        DailyGoalCard(
                            dailyGoal = state.dailyGoal,
                            onEditClick = { onAction(RunOverviewAction.OnEditGoalClick) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                item {
                    if(state.lastRun != null) {
                        LastRunCard(
                            runUi = state.lastRun,
                            date = state.lastRun.localDateTime.toLastRunDate(now).asString(),
                            onSeeAllRunsClick = { onAction(RunOverviewAction.OnSeeAllRunsClick) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    else {
                        Text(
                            text = stringResource(id = R.string.no_completed_runs_found),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp)
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = state.isAllRunsOpen,
            enter = slideInHorizontally { it },
            exit = slideOutHorizontally { it }
        ) {
            AllRunsPage(
                runs = state.runs,
                onBackClick = { onAction(RunOverviewAction.OnAllRunsBackClick) },
                onDeleteRunClick = { onAction(RunOverviewAction.OnDeleteRunClick(it)) }
            )
        }
        if(state.isGoalSheetOpen) {
            DailyGoalBottomSheet(
                draft = state.goalDraft,
                distanceUnit = state.distanceUnit,
                onDismiss = { onAction(RunOverviewAction.OnGoalSheetDismiss) },
                onMetricChange = { onAction(RunOverviewAction.OnGoalMetricChange(it)) },
                onDecrementClick = { onAction(RunOverviewAction.OnGoalDecrementClick) },
                onIncrementClick = { onAction(RunOverviewAction.OnGoalIncrementClick) },
                onPresetClick = { onAction(RunOverviewAction.OnGoalPresetClick(it)) },
                onDayToggle = { onAction(RunOverviewAction.OnGoalDayToggle(it)) },
                onSaveClick = { onAction(RunOverviewAction.OnSaveGoalClick) }
            )
        }
    }
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    SplitlineTheme {
        RunOverviewScreen(
            state = RunOverviewState(
                username = "Kyrylo",
                dailyGoal = DailyGoalProgress(
                    todayValue = 3_200L,
                    target = 5_000L,
                    week = listOf(
                        DayGoalStatus.MET,
                        DayGoalStatus.TODAY,
                        DayGoalStatus.UPCOMING,
                        DayGoalStatus.UPCOMING,
                        DayGoalStatus.UPCOMING,
                        DayGoalStatus.REST,
                        DayGoalStatus.REST
                    ),
                    streakDays = 12
                ).toDailyGoalUi(GoalMetric.DISTANCE, DistanceUnit.KILOMETERS, LocalDate.of(2026, 10, 27)),
                isDailyGoalLoaded = true
            ),
            onAction = {}
        )
    }
}

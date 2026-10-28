package com.kiras.run.presentation.run_overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.presentation.designsystem.RunIcon
import com.kiras.core.presentation.designsystem.RuniqueTheme
import com.kiras.core.presentation.designsystem.components.RuniqueFloatingActionButton
import com.kiras.core.presentation.designsystem.components.RuniqueScaffold
import com.kiras.run.presentation.R
import com.kiras.run.presentation.run_overview.components.HomeHeader
import com.kiras.run.presentation.run_overview.components.RunListItem
import com.kiras.run.presentation.util.toGreetingRes
import com.kiras.run.presentation.util.toHomeHeaderDate
import org.koin.compose.viewmodel.koinViewModel
import java.time.ZonedDateTime

@Composable
fun RunOverviewScreenRoot(
    onStartRunClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    viewModel: RunOverviewViewModel = koinViewModel()
) {
    RunOverviewScreen(
        state = viewModel.state,
        onAction = { action ->
            when(action) {
                RunOverviewAction.OnStartClick -> onStartRunClick()
                RunOverviewAction.OnLogOutClick -> onLogoutClick()
                RunOverviewAction.OnAnalyticsClick -> onAnalyticsClick()
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
    RuniqueScaffold(
        floatingActionButton = {
            RuniqueFloatingActionButton(
                icon = RunIcon,
                onClick = {
                    onAction(RunOverviewAction.OnStartClick)
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
                    onAnalyticsClick = { onAction(RunOverviewAction.OnAnalyticsClick) },
                    onLogoutClick = { onAction(RunOverviewAction.OnLogOutClick) },
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            if(state.runs.isNotEmpty()) {
                items(
                    items = state.runs,
                    key = { it.id }
                ) {
                    RunListItem(
                        runUi = it,
                        onDeleteClick = {
                            onAction(RunOverviewAction.OnDeleteRunClick(it))
                        }
                    )
                }
            }
            else {
                item {
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
}

@Preview
@Composable
private fun RunOverviewScreenPreview() {
    RuniqueTheme {
        RunOverviewScreen(
            state = RunOverviewState(username = "Kyrylo"),
            onAction = {}
        )
    }
}

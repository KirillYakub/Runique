package com.kiras.settings.presentation.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.settings.AppTheme
import com.kiras.core.domain.settings.CountdownDuration
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.GpsAccuracy
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineScaffold
import com.kiras.core.presentation.ui.ObserveAsEvents
import com.kiras.settings.presentation.R
import com.kiras.settings.presentation.settings.components.SettingsDivider
import com.kiras.settings.presentation.settings.components.SettingsOptionSheet
import com.kiras.settings.presentation.settings.components.SettingsRow
import com.kiras.settings.presentation.settings.components.SettingsSection
import com.kiras.settings.presentation.settings.components.SettingsSegmentedControl
import com.kiras.settings.presentation.settings.components.SettingsSwitch
import com.kiras.settings.presentation.settings.components.SettingsValue
import com.kiras.settings.presentation.util.toLabel
import com.kiras.settings.presentation.util.toLabelRes
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreenRoot(
    onHomeClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val versionName = remember {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName.orEmpty()
    }
    ObserveAsEvents(viewModel.events) { event ->
        when(event) {
            is SettingsEvent.Error -> {
                Toast.makeText(context, event.error.asString(context), Toast.LENGTH_LONG).show()
            }
        }
    }
    SettingsScreen(
        state = viewModel.state,
        versionName = versionName,
        onAction = { action ->
            when(action) {
                SettingsAction.OnHomeClick -> onHomeClick()
                SettingsAction.OnStatisticsClick -> onStatisticsClick()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsState,
    versionName: String,
    onAction: (SettingsAction) -> Unit
) {
    val settings = state.settings
    SplitlineScaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.settings),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.semantics { heading() }
            )
            SettingsSection(title = stringResource(R.string.units)) {
                SettingsRow(
                    title = stringResource(R.string.distance),
                    trailing = {
                        SettingsSegmentedControl(
                            options = DistanceUnit.entries,
                            selectedOption = settings.distanceUnit,
                            label = { stringResource(it.toLabelRes()) },
                            onOptionSelect = { onAction(SettingsAction.OnDistanceUnitChange(it)) }
                        )
                    }
                )
                SettingsDivider()
                SettingsRow(
                    title = stringResource(R.string.pace),
                    subtitle = stringResource(R.string.pace_subtitle),
                    trailing = {
                        SettingsSegmentedControl(
                            options = PaceFormat.entries,
                            selectedOption = settings.paceFormat,
                            label = { stringResource(it.toLabelRes(settings.distanceUnit)) },
                            onOptionSelect = { onAction(SettingsAction.OnPaceFormatChange(it)) }
                        )
                    }
                )
            }
            SettingsSection(title = stringResource(R.string.run_tracking)) {
                SettingsRow(
                    title = stringResource(R.string.countdown),
                    subtitle = stringResource(R.string.countdown_subtitle),
                    onClick = { onAction(SettingsAction.OnCountdownClick) },
                    trailing = { SettingsValue(value = settings.countdown.toLabel().asString()) }
                )
                SettingsDivider()
                SettingsRow(
                    title = stringResource(R.string.keep_screen_on),
                    subtitle = stringResource(R.string.keep_screen_on_subtitle),
                    modifier = Modifier.toggleable(
                        value = settings.keepScreenOn,
                        role = Role.Switch,
                        onValueChange = { onAction(SettingsAction.OnKeepScreenOnToggle(it)) }
                    ),
                    trailing = { SettingsSwitch(isChecked = settings.keepScreenOn) }
                )
                SettingsDivider()
                SettingsRow(
                    title = stringResource(R.string.gps_accuracy),
                    subtitle = stringResource(R.string.gps_accuracy_subtitle),
                    onClick = { onAction(SettingsAction.OnGpsAccuracyClick) },
                    trailing = { SettingsValue(value = stringResource(settings.gpsAccuracy.toLabelRes())) }
                )
            }
            SettingsSection(title = stringResource(R.string.appearance)) {
                SettingsRow(
                    title = stringResource(R.string.theme),
                    subtitle = stringResource(R.string.theme_subtitle),
                    bottom = {
                        SettingsSegmentedControl(
                            options = AppTheme.entries,
                            selectedOption = settings.theme,
                            label = { stringResource(it.toLabelRes()) },
                            onOptionSelect = { onAction(SettingsAction.OnThemeChange(it)) },
                            isFullWidth = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )
            }
            Text(
                text = stringResource(R.string.version_footer, versionName),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    when(state.openPicker) {
        SettingsPicker.COUNTDOWN -> {
            SettingsOptionSheet(
                title = stringResource(R.string.countdown),
                options = CountdownDuration.entries,
                selectedOption = settings.countdown,
                label = { it.toLabel().asString() },
                onOptionSelect = { onAction(SettingsAction.OnCountdownSelect(it)) },
                onDismiss = { onAction(SettingsAction.OnPickerDismiss) }
            )
        }
        SettingsPicker.GPS_ACCURACY -> {
            SettingsOptionSheet(
                title = stringResource(R.string.gps_accuracy),
                options = GpsAccuracy.entries,
                selectedOption = settings.gpsAccuracy,
                label = { stringResource(it.toLabelRes()) },
                onOptionSelect = { onAction(SettingsAction.OnGpsAccuracySelect(it)) },
                onDismiss = { onAction(SettingsAction.OnPickerDismiss) }
            )
        }
        null -> Unit
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    SplitlineTheme(isDarkTheme = true) {
        SettingsScreen(
            state = SettingsState(),
            versionName = "1.0.0",
            onAction = {}
        )
    }
}

package com.kiras.run.presentation.active_run

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kiras.core.domain.location.Location
import com.kiras.core.presentation.designsystem.SplitlineTheme
import com.kiras.core.presentation.designsystem.components.SplitlineActionButton
import com.kiras.core.presentation.designsystem.components.SplitlineDialog
import com.kiras.core.presentation.designsystem.components.SplitlineOutlinedActionButton
import com.kiras.core.presentation.ui.ObserveAsEvents
import com.kiras.run.domain.RunData
import com.kiras.run.presentation.R
import com.kiras.run.presentation.active_run.components.ActiveRunSheetContent
import com.kiras.run.presentation.active_run.components.CountdownOverlay
import com.kiras.run.presentation.active_run.maps.TrackerMap
import com.kiras.run.presentation.active_run.service.ActiveRunService
import com.kiras.run.presentation.util.hasLocationPermission
import com.kiras.run.presentation.util.hasNotificationPermission
import com.kiras.run.presentation.util.shouldShowLocationPermissionRationale
import com.kiras.run.presentation.util.shouldShowNotificationPermissionRationale
import org.koin.compose.viewmodel.koinViewModel
import java.io.ByteArrayOutputStream
import kotlin.time.Duration.Companion.minutes

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun ActiveRunScreenRoot(
    onBack: () -> Unit,
    onFinish: () -> Unit,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    viewModel: ActiveRunViewModel = koinViewModel()
) {
    val context = LocalContext.current
    ObserveAsEvents(flow = viewModel.events) { event ->
        when(event) {
            is ActiveRunEvent.Error -> {
                Toast.makeText(
                    context,
                    event.error.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }
            is ActiveRunEvent.TrySaveRunWithoutMove -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.move_was_not_started_for_save),
                    Toast.LENGTH_LONG
                ).show()

                onBack()
            }
            ActiveRunEvent.RunSaved -> {
                onFinish()
            }
        }
    }

    ActiveRunScreen(
        state = viewModel.state,
        onServiceToggle = onServiceToggle,
        onAction = { action ->
            when(action) {
                is ActiveRunAction.OnBackRunClick -> {
                    if(!viewModel.state.hasStartedRunning) {
                        onBack()
                    }
                }
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActiveRunScreen(
    state: ActiveRunState,
    onServiceToggle: (isServiceRunning: Boolean) -> Unit,
    onAction: (ActiveRunAction) -> Unit
) {
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { perms ->
        val hasCourseLocationPermission = perms[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val hasFineLocationPermission = perms[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val hasNotificationPermission = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perms[Manifest.permission.POST_NOTIFICATIONS] ?: false
        }
        else true

        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.OnSubmitLocationPermissionInfo(
                acceptedLocationPermission = hasCourseLocationPermission && hasFineLocationPermission,
                showLocationPermissionRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.OnSubmitNotificationPermissionInfo(
                acceptedNotificationPermission = hasNotificationPermission,
                showNotificationPermissionRationale = showNotificationRationale
            )
        )
    }

    LaunchedEffect(key1 = true) {
        val activity = context as ComponentActivity
        val showLocationRationale = activity.shouldShowLocationPermissionRationale()
        val showNotificationRationale = activity.shouldShowNotificationPermissionRationale()

        onAction(
            ActiveRunAction.OnSubmitLocationPermissionInfo(
                acceptedLocationPermission = context.hasLocationPermission(),
                showLocationPermissionRationale = showLocationRationale
            )
        )
        onAction(
            ActiveRunAction.OnSubmitNotificationPermissionInfo(
                acceptedNotificationPermission = context.hasNotificationPermission(),
                showNotificationPermissionRationale = showNotificationRationale
            )
        )
        if(!showLocationRationale && !showNotificationRationale) {
            permissionLauncher.requestSplitlinePermissions(context)
        }
    }

    LaunchedEffect(key1 = state.isRunFinished) {
        if(state.isRunFinished) {
            onServiceToggle(false)
        }
    }
    LaunchedEffect(key1 = state.shouldTrack) {
        if(context.hasLocationPermission() && state.shouldTrack && !ActiveRunService.isServiceActive) {
            onServiceToggle(true)
        }
    }

    val view = LocalView.current
    DisposableEffect(state.keepScreenOn) {
        view.keepScreenOn = state.keepScreenOn
        onDispose {
            view.keepScreenOn = false
        }
    }

    BackHandler {
        onAction(ActiveRunAction.OnBackRunClick)
    }

    val density = LocalDensity.current
    var peekContentHeightPx by remember { mutableIntStateOf(0) }
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val sheetPeekHeight = with(density) { peekContentHeightPx.toDp() } + 44.dp + navigationBarHeight

    BottomSheetScaffold(
        scaffoldState = rememberBottomSheetScaffoldState(
            bottomSheetState = rememberStandardBottomSheetState(skipHiddenState = true)
        ),
        sheetPeekHeight = sheetPeekHeight,
        sheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetDragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outline)
            )
        },
        sheetContent = {
            ActiveRunSheetContent(
                distanceMeters = state.runData.distanceMeters,
                elapsedTime = state.elapsedTime,
                maxSpeedKmh = state.maxSpeedKmh,
                distanceUnit = state.distanceUnit,
                paceFormat = state.paceFormat,
                isRunning = state.hasStartedRunning,
                onStartClick = { onAction(ActiveRunAction.OnStartRunClick) },
                onStopClick = { onAction(ActiveRunAction.OnStopRunClick) },
                onPeekContentHeightChange = { peekContentHeightPx = it }
            )
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            TrackerMap(
                isRunFinished = state.isRunFinished,
                currentLocation = state.currentLocation,
                locations = state.runData.locations,
                onSnapshot = { bmp ->
                    val stream = ByteArrayOutputStream()
                    stream.use {
                        bmp.compress(Bitmap.CompressFormat.JPEG, 80, it)
                    }
                    onAction(ActiveRunAction.OnRunProcessed(stream.toByteArray()))
                },
                contentPadding = PaddingValues(
                    top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                    bottom = sheetPeekHeight
                ),
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    if(state.countdownRemaining != null) {
        CountdownOverlay(
            remainingSeconds = state.countdownRemaining,
            onCancelClick = { onAction(ActiveRunAction.OnCancelCountdownClick) }
        )
    }

    if(state.showFinishDialog) {
        SplitlineDialog(
            title = stringResource(R.string.finish_run_title),
            onDismiss = { onAction(ActiveRunAction.OnDismissFinishDialog) },
            description = stringResource(R.string.finish_run_description),
            primaryButton = {
                SplitlineActionButton(
                    text = stringResource(R.string.finish),
                    isLoading = state.isSavingRun,
                    onClick = { onAction(ActiveRunAction.OnFinishRunClick) },
                    modifier = Modifier.weight(1f)
                )
            },
            secondaryButton = {
                SplitlineOutlinedActionButton(
                    text = stringResource(R.string.keep_running),
                    isLoading = false,
                    onClick = { onAction(ActiveRunAction.OnDismissFinishDialog) },
                    modifier = Modifier.weight(1f)
                )
            }
        )
    }

    if(state.showLocationPermissionRationale || state.showNotificationPermissionRationale) {
        SplitlineDialog(
            title = stringResource(R.string.permission_required_title),
            onDismiss = { },
            description = when {
                state.showLocationPermissionRationale && state.showNotificationPermissionRationale -> {
                    stringResource(R.string.location_notification_rationale)
                }
                state.showLocationPermissionRationale -> {
                    stringResource(R.string.location_rationale)
                }
                else -> {
                    stringResource(R.string.notification_rationale)
                }
            },
            primaryButton = {
                SplitlineOutlinedActionButton(
                    text = stringResource(R.string.okay),
                    isLoading = false,
                    onClick = {
                        onAction(ActiveRunAction.OnDismissRationaleDialog)
                        permissionLauncher.requestSplitlinePermissions(context)
                    }
                )
            }
        )
    }
}

private fun ActivityResultLauncher<Array<String>>.requestSplitlinePermissions(
    context: Context
) {
    val hasLocationPermission = context.hasLocationPermission()
    val hasNotificationPermission = context.hasNotificationPermission()

    val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    val notificationPermission = if(Build.VERSION.SDK_INT >= 33) {
        arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    }
    else arrayOf()

    when {
        !hasLocationPermission && !hasNotificationPermission -> {
            launch(locationPermissions + notificationPermission)
        }
        !hasLocationPermission -> {
            launch(locationPermissions)
        }
        !hasNotificationPermission -> {
            launch(notificationPermission)
        }
    }
}

@Preview
@Composable
private fun ActiveRunScreenPreview() {
    SplitlineTheme {
        ActiveRunScreen(
            state = ActiveRunState(
                elapsedTime = 20.minutes,
                runData = RunData(distanceMeters = 3860),
                maxSpeedKmh = 14.2,
                shouldTrack = true,
                hasStartedRunning = true,
                currentLocation = Location(50.1, 14.4)
            ),
            onServiceToggle = {},
            onAction = {}
        )
    }
}
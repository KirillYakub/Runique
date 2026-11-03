package com.kiras.run.presentation.active_run

import com.kiras.core.domain.location.Location
import com.kiras.core.domain.settings.CountdownDuration
import com.kiras.core.domain.settings.DistanceUnit
import com.kiras.core.domain.settings.PaceFormat
import com.kiras.run.domain.RunData
import kotlin.time.Duration

data class ActiveRunState(
    val elapsedTime: Duration = Duration.ZERO,
    val runData: RunData = RunData(),
    val shouldTrack: Boolean = false,
    val hasStartedRunning: Boolean = false,
    val currentLocation: Location? = null,
    val isRunFinished: Boolean = false,
    val isSavingRun: Boolean = false,
    val showLocationPermissionRationale: Boolean = false,
    val showNotificationPermissionRationale: Boolean = false,
    val distanceUnit: DistanceUnit = DistanceUnit.KILOMETERS,
    val paceFormat: PaceFormat = PaceFormat.PACE,
    val countdown: CountdownDuration = CountdownDuration.OFF,
    val countdownRemaining: Int? = null,
    val keepScreenOn: Boolean = false
)

package com.kiras.run.presentation.active_run

sealed interface ActiveRunAction {
    data object OnStartRunClick: ActiveRunAction
    data object OnStopRunClick: ActiveRunAction
    data object OnCancelCountdownClick: ActiveRunAction
    data object OnFinishRunClick: ActiveRunAction
    data object OnDismissFinishDialog: ActiveRunAction
    data object OnBackRunClick: ActiveRunAction
    data class OnSubmitLocationPermissionInfo(
        val acceptedLocationPermission: Boolean,
        val showLocationPermissionRationale: Boolean
    ): ActiveRunAction
    data class OnSubmitNotificationPermissionInfo(
        val acceptedNotificationPermission: Boolean,
        val showNotificationPermissionRationale: Boolean
    ): ActiveRunAction
    data object OnDismissRationaleDialog: ActiveRunAction
    class OnRunProcessed(val mapPictureBytes: ByteArray): ActiveRunAction
}

package com.kiras.run.presentation.active_run

sealed interface ActiveRunAction {
    data object OnToggleRunClick: ActiveRunAction
    data object OnFinishRunClick: ActiveRunAction
    data object OnResumeRunClick: ActiveRunAction
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
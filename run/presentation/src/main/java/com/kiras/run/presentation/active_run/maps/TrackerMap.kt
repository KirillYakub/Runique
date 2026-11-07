package com.kiras.run.presentation.active_run.maps

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapEffect
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.google.maps.android.ktx.awaitSnapshot
import com.kiras.core.domain.location.Location
import com.kiras.core.domain.location.LocationTimestamp
import com.kiras.core.presentation.designsystem.LocalIsDarkTheme
import com.kiras.run.presentation.R
import com.kiras.run.presentation.active_run.model.RunSplitUi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TrackerMap(
    isRunFinished: Boolean,
    currentLocation: Location?,
    locations: List<List<LocationTimestamp>>,
    splits: List<RunSplitUi>,
    onSnapshot: (Bitmap) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val context = LocalContext.current
    val isDarkTheme = LocalIsDarkTheme.current
    val mapStyle = remember(isDarkTheme) {
        MapStyleOptions.loadRawResourceStyle(
            context,
            if(isDarkTheme) R.raw.map_style_night else R.raw.map_style_day
        )
    }
    val cameraPositionState = rememberCameraPositionState()
    val marketState = rememberUpdatedMarkerState()

    val startPoint = locations.firstOrNull()?.firstOrNull()?.location?.location
    val endPoint = locations.lastOrNull()?.lastOrNull()?.location?.location
    val startMarkerState = rememberUpdatedMarkerState(
        position = LatLng(startPoint?.lat ?: 0.0, startPoint?.long ?: 0.0)
    )
    val endMarkerState = rememberUpdatedMarkerState(
        position = LatLng(endPoint?.lat ?: 0.0, endPoint?.long ?: 0.0)
    )
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val ringColor = MaterialTheme.colorScheme.background
    val splitNumberColor = MaterialTheme.colorScheme.onSurface

    val markerPositionLat by animateFloatAsState(
        targetValue = currentLocation?.lat?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500)
    )
    val markerPositionLong by animateFloatAsState(
        targetValue = currentLocation?.long?.toFloat() ?: 0f,
        animationSpec = tween(durationMillis = 500)
    )
    val markerPosition = remember(markerPositionLat, markerPositionLong) {
        LatLng(markerPositionLat.toDouble(), markerPositionLong.toDouble())
    }

    var mapLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(markerPosition, isRunFinished) {
        if(!isRunFinished) {
            marketState.position = markerPosition
        }
    }
    LaunchedEffect(currentLocation, isRunFinished) {
        if(mapLoaded && currentLocation != null && !isRunFinished) {
            val latLng = LatLng(currentLocation.lat, currentLocation.long)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            )
        }
    }

    var triggerCapture by remember { mutableStateOf(false) }
    var createSnapshotJob: Job? = remember { null }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        contentPadding = if(isRunFinished) PaddingValues() else contentPadding,
        properties = MapProperties(
            mapStyleOptions = mapStyle
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false
        ),
        onMapLoaded = {
            mapLoaded = true
        },
        modifier = if(isRunFinished) {
            modifier
                .width(300.dp)
                .aspectRatio(16 / 9f)
                .alpha(0f)
                .onSizeChanged {
                    if(it.width >= 300)
                        triggerCapture = true
                }
        } else modifier
    ) {
        SplitlinePolylines(locations = locations)

        MapEffect(locations, isRunFinished, triggerCapture) { map ->
            if(isRunFinished && triggerCapture && createSnapshotJob == null) {
                triggerCapture = false

                val boundsBuilder = LatLngBounds.builder()
                locations.flatten().forEach { location ->
                    boundsBuilder
                        .include(
                            LatLng(
                                location.location.location.lat,
                                location.location.location.long
                            )
                        )
                }
                map.moveCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        boundsBuilder.build(),
                        100
                    )
                )
                map.setOnCameraIdleListener {
                    createSnapshotJob?.cancel()
                    createSnapshotJob = GlobalScope.launch {
                        delay(500)
                        map.awaitSnapshot()?.let(onSnapshot)
                    }
                }
            }
        }

        splits.forEach { split ->
            key(split.number) {
                MarkerComposable(
                    split.number,
                    secondaryColor,
                    ringColor,
                    splitNumberColor,
                    state = rememberUpdatedMarkerState(
                        position = LatLng(split.endLocation.lat, split.endLocation.long)
                    ),
                    anchor = Offset(0.5f, 0.5f)
                ) {
                    SplitMarker(number = split.number)
                }
            }
        }

        if(startPoint != null) {
            MarkerComposable(
                secondaryColor,
                ringColor,
                state = startMarkerState,
                anchor = Offset(0.5f, 0.5f)
            ) {
                RouteDot(
                    color = secondaryColor,
                    ringWidth = 2.5.dp,
                    showHalo = false
                )
            }
        }

        if(isRunFinished && endPoint != null) {
            MarkerComposable(
                primaryColor,
                ringColor,
                state = endMarkerState,
                anchor = Offset(0.5f, 0.5f)
            ) {
                RouteDot(
                    color = primaryColor,
                    ringWidth = 3.dp,
                    showHalo = false
                )
            }
        }

        if(!isRunFinished && currentLocation != null) {
            MarkerComposable(
                primaryColor,
                ringColor,
                state = marketState,
                anchor = Offset(0.5f, 0.5f),
                zIndex = 1f
            ) {
                RouteDot(
                    color = primaryColor,
                    ringWidth = 3.dp,
                    showHalo = true
                )
            }
        }
    }
}
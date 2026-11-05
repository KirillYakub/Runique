package com.kiras.run.presentation.active_run.maps

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.Polyline
import com.kiras.core.domain.location.LocationTimestamp

@Composable
fun SplitlinePolylines(locations: List<List<LocationTimestamp>>) {
    val segments = remember(locations) {
        locations.map { segment ->
            segment.map { LatLng(it.location.location.lat, it.location.location.long) }
        }
    }
    val density = LocalDensity.current
    val outlineWidth = with(density) { 11.dp.toPx() }
    val lineWidth = with(density) { 6.dp.toPx() }
    val outlineColor = MaterialTheme.colorScheme.background.copy(alpha = 0.6f)
    val lineColor = MaterialTheme.colorScheme.primary

    segments.forEach { points ->
        if(points.size < 2) return@forEach
        Polyline(
            points = points,
            color = outlineColor,
            width = outlineWidth,
            jointType = JointType.ROUND,
            startCap = RoundCap(),
            endCap = RoundCap(),
            zIndex = 0f
        )
        Polyline(
            points = points,
            color = lineColor,
            width = lineWidth,
            jointType = JointType.ROUND,
            startCap = RoundCap(),
            endCap = RoundCap(),
            zIndex = 1f
        )
    }
}

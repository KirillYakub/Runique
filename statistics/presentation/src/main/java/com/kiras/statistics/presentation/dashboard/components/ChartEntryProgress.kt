package com.kiras.statistics.presentation.dashboard.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode

@Composable
fun rememberChartEntryProgress(): Animatable<Float, AnimationVector1D> {
    val isInPreview = LocalInspectionMode.current
    val progress = remember { Animatable(if(isInPreview) 1f else 0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
    }
    return progress
}

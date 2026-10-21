package com.kiras.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = RuniqueGreen,
    background = RuniqueBlack,
    surface = RuniqueDarkGray,
    surfaceVariant = RuniqueSurfaceElevated,
    surfaceContainerLowest = RuniqueSurfaceSecondary,
    surfaceContainerLow = RuniqueSurfaceSecondary,
    surfaceContainer = RuniqueDarkGray,
    surfaceContainerHigh = RuniqueSurfaceElevated,
    surfaceContainerHighest = RuniqueSurfaceSubtle,
    secondary = RuniqueBlue,
    tertiary = RuniqueOrange,
    primaryContainer = RuniqueGreenDark,
    onPrimaryContainer = RuniqueGreen,
    onPrimary = RuniqueBlack,
    onBackground = RuniqueWhite,
    onSurface = RuniqueWhite,
    onSurfaceVariant = RuniqueGray,
    outline = RuniqueBorder,
    outlineVariant = RuniqueBorder,
    error = RuniqueDarkRed,
    errorContainer = RuniqueDarkRed5
)

@Composable
fun RuniqueTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
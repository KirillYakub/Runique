package com.kiras.core.presentation.designsystem

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val DarkColorScheme = darkColorScheme(
    primary = SplitlineGreen,
    background = SplitlineBlack,
    surface = SplitlineDarkGray,
    surfaceVariant = SplitlineSurfaceElevated,
    surfaceContainerLowest = SplitlineSurfaceSecondary,
    surfaceContainerLow = SplitlineSurfaceSecondary,
    surfaceContainer = SplitlineDarkGray,
    surfaceContainerHigh = SplitlineSurfaceElevated,
    surfaceContainerHighest = SplitlineSurfaceSubtle,
    secondary = SplitlineBlue,
    tertiary = SplitlineOrange,
    primaryContainer = SplitlineGreenDark,
    onPrimaryContainer = SplitlineGreen,
    onPrimary = SplitlineBlack,
    onBackground = SplitlineWhite,
    onSurface = SplitlineWhite,
    onSurfaceVariant = SplitlineGray,
    outline = SplitlineBorder,
    outlineVariant = SplitlineBorder,
    error = SplitlineDarkRed,
    errorContainer = SplitlineDarkRed5
)

@Composable
fun SplitlineTheme(
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
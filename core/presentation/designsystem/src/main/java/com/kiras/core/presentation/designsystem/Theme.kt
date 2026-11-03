package com.kiras.core.presentation.designsystem

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

val LightColorScheme = lightColorScheme(
    primary = SplitlineGreen,
    background = SplitlineDayBackground,
    surface = SplitlineDaySurface,
    surfaceVariant = SplitlineDaySurfaceVariant,
    surfaceContainerLowest = SplitlineDaySurface,
    surfaceContainerLow = SplitlineDayBackground,
    surfaceContainer = SplitlineDaySurface,
    surfaceContainerHigh = SplitlineDaySurfaceVariant,
    surfaceContainerHighest = SplitlineDaySurfaceSubtle,
    secondary = SplitlineBlue,
    tertiary = SplitlineDayOrange,
    primaryContainer = SplitlineDayGreenContainer,
    onPrimaryContainer = SplitlineDayGreenDark,
    onPrimary = SplitlineDayOnPrimary,
    onBackground = SplitlineDayOnSurface,
    onSurface = SplitlineDayOnSurface,
    onSurfaceVariant = SplitlineDayOnSurfaceVariant,
    outline = SplitlineDayOutline,
    outlineVariant = SplitlineDayOutline,
    error = SplitlineDarkRed,
    errorContainer = SplitlineDarkRed5
)

@Composable
fun SplitlineTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if(isDarkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isDarkTheme
                isAppearanceLightNavigationBars = !isDarkTheme
            }
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

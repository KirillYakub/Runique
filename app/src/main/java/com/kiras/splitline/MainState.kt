package com.kiras.splitline

import com.kiras.core.domain.settings.AppTheme

data class MainState(
    val isLoggedIn: Boolean = false,
    val isCheckingAuth: Boolean = false,
    val theme: AppTheme = AppTheme.SYSTEM,
    val isThemeLoaded: Boolean = false
)

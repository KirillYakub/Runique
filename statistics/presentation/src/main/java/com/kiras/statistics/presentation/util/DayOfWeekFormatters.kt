package com.kiras.statistics.presentation.util

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

fun DayOfWeek.toNarrowLabel(): String {
    return getDisplayName(TextStyle.NARROW, Locale.getDefault())
}

fun DayOfWeek.toFullLabel(): String {
    return getDisplayName(TextStyle.FULL, Locale.getDefault())
}

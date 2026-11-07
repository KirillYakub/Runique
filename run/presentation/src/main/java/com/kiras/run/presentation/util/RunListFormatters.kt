package com.kiras.run.presentation.util

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.time.Duration

fun ZonedDateTime.toRunDateTitle(): String {
    return DateTimeFormatter
        .ofPattern("EEE, d MMM", Locale.getDefault())
        .format(this)
}

fun ZonedDateTime.toRunStartTime(): String {
    return DateTimeFormatter
        .ofPattern("HH:mm", Locale.getDefault())
        .format(this)
}

fun Duration.toShortDurationText(): String {
    val totalSeconds = inWholeSeconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if(hours > 0) {
        String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
    }
}

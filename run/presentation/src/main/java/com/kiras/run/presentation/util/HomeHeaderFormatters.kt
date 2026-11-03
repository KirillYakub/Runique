package com.kiras.run.presentation.util

import androidx.annotation.StringRes
import com.kiras.run.presentation.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun ZonedDateTime.toHomeHeaderDate(): String {
    return DateTimeFormatter
        .ofPattern("EEEE, d MMM", Locale.getDefault())
        .format(this)
}

@StringRes
fun ZonedDateTime.toGreetingRes(): Int {
    return when(hour) {
        in 5..11 -> R.string.good_morning
        in 12..17 -> R.string.good_afternoon
        else -> R.string.good_evening
    }
}

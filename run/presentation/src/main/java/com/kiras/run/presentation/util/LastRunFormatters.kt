package com.kiras.run.presentation.util

import com.kiras.core.presentation.ui.UiText
import com.kiras.run.presentation.R
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

fun ZonedDateTime.toLastRunDate(now: ZonedDateTime): UiText {
    val time = DateTimeFormatter
        .ofPattern("HH:mm", Locale.getDefault())
        .format(this)
    val daysAgo = ChronoUnit.DAYS.between(toLocalDate(), now.toLocalDate())
    return when(daysAgo) {
        0L -> UiText.StringResource(R.string.today_at, arrayOf(time))
        1L -> UiText.StringResource(R.string.yesterday_at, arrayOf(time))
        else -> UiText.DynamicString(
            DateTimeFormatter
                .ofPattern("d MMM · HH:mm", Locale.getDefault())
                .format(this)
        )
    }
}

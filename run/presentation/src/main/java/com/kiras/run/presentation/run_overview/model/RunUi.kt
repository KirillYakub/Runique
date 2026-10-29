package com.kiras.run.presentation.run_overview.model

import java.time.ZonedDateTime

data class RunUi(
    val id: String,
    val duration: String,
    val dateTime: String,
    val localDateTime: ZonedDateTime,
    val distance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String,
    val totalElevation: String,
    val mapPictureUrl: String?
)

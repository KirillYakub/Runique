package com.kiras.run.presentation.run_overview.model

import java.time.ZonedDateTime

data class RunUi(
    val id: String,
    val duration: String,
    val shortDuration: String,
    val dateTitle: String,
    val startTime: String,
    val localDateTime: ZonedDateTime,
    val distance: String,
    val preciseDistance: String,
    val avgSpeed: String,
    val maxSpeed: String,
    val pace: String?,
    val averagePace: String,
    val totalElevation: String,
    val mapPictureUrl: String?,
    val splits: RunSplitsUi?
)

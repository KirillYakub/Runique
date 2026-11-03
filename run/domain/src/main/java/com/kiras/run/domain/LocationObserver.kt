package com.kiras.run.domain

import com.kiras.core.domain.location.LocationWithAltitude
import com.kiras.core.domain.settings.GpsAccuracy
import kotlinx.coroutines.flow.Flow

interface LocationObserver {
    fun observeLocation(accuracy: GpsAccuracy): Flow<LocationWithAltitude>
}

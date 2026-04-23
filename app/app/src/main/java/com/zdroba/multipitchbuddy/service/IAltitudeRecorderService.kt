package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import kotlinx.coroutines.flow.Flow

interface IAltitudeRecorderService {
    val readings: Flow<AltitudeReading?>
    val lastAltitude: Double?

    suspend fun start()
    suspend fun end()
}
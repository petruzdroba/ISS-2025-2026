package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import kotlinx.coroutines.flow.Flow

interface IAltitudeRecorderService {
    val readings: Flow<AltitudeReading?>

    suspend fun start()
    suspend fun end()
}
package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import kotlinx.coroutines.flow.Flow

interface IClimbEventService {
    val lastEvent: Flow<ClimbEvent?>

    suspend fun processReading(reading: AltitudeReading): ClimbEvent?
    suspend fun save(event: ClimbEvent)

    fun setSession(sessionId: Long)
}
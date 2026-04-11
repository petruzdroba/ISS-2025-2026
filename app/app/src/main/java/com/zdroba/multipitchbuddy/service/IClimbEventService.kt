package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent
import kotlinx.coroutines.flow.Flow

interface IClimbEventService {
    val lastEvent: Flow<ClimbEvent?>

    suspend fun start()
    suspend fun stop()
    suspend fun save(event: ClimbEvent)
}
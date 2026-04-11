package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.service.classification.ClassificationStrategy
import kotlinx.coroutines.flow.Flow

interface IClimbEventService: ClassificationStrategy {
    val lastEvent: Flow<ClimbEvent?>

    suspend fun start()
    suspend fun stop()
    suspend fun save(event: ClimbEvent)
}
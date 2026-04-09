package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.service.classification.ClassificationStrategy

interface IClimbEventService: ClassificationStrategy {

    suspend fun start()

    suspend fun stop()

    suspend fun save(event: ClimbEvent)
}
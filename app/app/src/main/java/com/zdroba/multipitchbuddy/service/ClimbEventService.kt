package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.repository.IClimbEventRepository
import com.zdroba.multipitchbuddy.service.classification.ClassificationStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ClimbEventService(
    private val strategy: ClassificationStrategy,
    private val repository: IClimbEventRepository
): IClimbEventService {

    private var sessionId: Long = 0

    private val _lastEvent = MutableStateFlow<ClimbEvent?>(null)
    override val lastEvent: Flow<ClimbEvent?> = _lastEvent.asStateFlow()

    private val shortWindow = mutableListOf<AltitudeReading>()
    private val longWindow = mutableListOf<AltitudeReading>()

    override suspend fun save(event: ClimbEvent) {
        repository.save(event)
    }

    override suspend fun processReading(reading: AltitudeReading): ClimbEvent? {
        shortWindow.add(reading)
        longWindow.add(reading)

        if (shortWindow.size > strategy.shortWindowSize)
            shortWindow.removeAt(0)
        if (longWindow.size > strategy.longWindowSize)
            longWindow.removeAt(0)

        val event = if (shortWindow.size == strategy.shortWindowSize) {
            strategy.classifyShort(shortWindow.toList())
                ?: if (longWindow.size == strategy.longWindowSize)
                    strategy.classifyLong(longWindow.toList())
                else null
        } else if (longWindow.size == strategy.longWindowSize) {
            strategy.classifyLong(longWindow.toList())
        } else null

        event ?: return null

        val climbEvent = ClimbEvent(
            time = reading.time,
            altitude = reading.altitude,
            event = event,
            sessionId = sessionId
        )

        _lastEvent.value = climbEvent
        save(climbEvent)
        return climbEvent
    }

    fun setSession(sessionId: Long) {
        this.sessionId = sessionId
    }
}
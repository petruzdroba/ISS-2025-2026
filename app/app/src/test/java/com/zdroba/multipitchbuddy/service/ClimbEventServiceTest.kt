package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event
import com.zdroba.multipitchbuddy.repository.IClimbEventRepository
import com.zdroba.multipitchbuddy.service.classification.ClassificationStrategy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant

class ClimbEventServiceTest {

    private val savedEvents = mutableListOf<ClimbEvent>()

    private val mockRepo = object : IClimbEventRepository {
        override suspend fun save(entity: ClimbEvent): Long {
            savedEvents.add(entity)
            return 1L;
        }

        override suspend fun update(entity: ClimbEvent) {
        }

        override suspend fun delete(id: Long) {
        }

        override suspend fun getById(id: Long): ClimbEvent {
            return savedEvents.first { it.id == id }
        }

        override suspend fun getAll(): List<ClimbEvent> {
            return TODO("Provide the return value")

        }

        override suspend fun getBySessionId(key: Long): List<ClimbEvent> {
            return TODO("Provide the return value")
        }
    }

    private fun makeReading(altitude: Double) =
        AltitudeReading(altitude, Instant.now())

    private fun mockStrategy(
        shortSize: Int = 3,
        longSize: Int = 6,
        shortResult: Event? = null,
        longResult: Event? = null
    ) = object : ClassificationStrategy {
        override val shortWindowSize = shortSize
        override val longWindowSize = longSize
        override val fallDropThreshold = 0.4
        override val allowedNoiseThreshold = 0.4
        override val restMaxChangeThreshold = 0.25
        override val pitchAltitudeThreshold = 8.0
        override val pitchRestTimeThreshold = 10L
        override fun classifyShort(readings: List<AltitudeReading>) = shortResult
        override fun classifyLong(readings: List<AltitudeReading>) = longResult
    }

    @Test
    fun `emits nothing before window is full`() = runBlocking {
        val service = ClimbEventService(mockStrategy(), mockRepo)
        service.setSession(1L)
        service.processReading(makeReading(1.0))
        service.processReading(makeReading(2.0))
        assertNull(service.lastEvent.first())
    }

    @Test
    fun `emits event and saves to repo when short window classifies`() = runBlocking {
        val service = ClimbEventService(mockStrategy(shortSize = 3, shortResult = Event.FALL), mockRepo)
        service.setSession(42L)
        service.processReading(makeReading(1.0))
        service.processReading(makeReading(2.0))
        service.processReading(makeReading(3.0))

        val event = service.lastEvent.first()
        assertEquals(Event.FALL, event?.event)
        assertEquals(42L, event?.sessionId)
        assertEquals(1, savedEvents.size)
        assertEquals(42L, savedEvents.first().sessionId)
    }

    @Test
    fun `returns null when classifier returns null`() = runBlocking {
        val service = ClimbEventService(mockStrategy(shortSize = 3, shortResult = null), mockRepo)
        service.setSession(1L)
        service.processReading(makeReading(1.0))
        service.processReading(makeReading(2.0))
        val result = service.processReading(makeReading(3.0))

        assertNull(result)
        assertEquals(0, savedEvents.size)
    }

    @Test
    fun `sessionId is correctly attached to saved event`() = runBlocking {
        val service = ClimbEventService(mockStrategy(shortSize = 3, shortResult = Event.REST), mockRepo)
        service.setSession(99L)
        repeat(3) { service.processReading(makeReading(it.toDouble())) }

        assertEquals(99L, savedEvents.first().sessionId)
    }

    @Test
    fun `emits event from long window when short window returns null`() = runBlocking {
        val service = ClimbEventService(
            mockStrategy(shortSize = 3, longSize = 6, shortResult = null, longResult = Event.PITCH_CHANGED),
            mockRepo
        )
        service.setSession(1L)
        repeat(6) { service.processReading(makeReading(it.toDouble())) }

        assertEquals(Event.PITCH_CHANGED, service.lastEvent.first()?.event)
        assertEquals(1, savedEvents.size)
    }

    @Test
    fun `short window takes priority over long window`() = runBlocking {
        val service = ClimbEventService(
            mockStrategy(shortSize = 3, longSize = 6, shortResult = Event.FALL, longResult = Event.PITCH_CHANGED),
            mockRepo
        )
        service.setSession(1L)
        repeat(6) { service.processReading(makeReading(it.toDouble())) }

        // should be FALL not PITCH_CHANGED since short fires first
        assertEquals(Event.FALL, service.lastEvent.first()?.event)
    }

    @Test
    fun `emits multiple events over time`() = runBlocking {
        val service = ClimbEventService(
            mockStrategy(shortSize = 3, shortResult = Event.REST),
            mockRepo
        )
        service.setSession(1L)
        repeat(9) { service.processReading(makeReading(it.toDouble())) }

        // sliding window emits on every reading after window is full = 7 events (readings 3-9)
        assertEquals(7, savedEvents.size)
    }

    @Test
    fun `last event is overwritten by newer event`() = runBlocking {
        var callCount = 0
        val service = ClimbEventService(
            mockStrategy(shortSize = 3, shortResult = null).let {
                object : ClassificationStrategy by it {
                    override fun classifyShort(readings: List<AltitudeReading>): Event? {
                        callCount++
                        return if (callCount >= 2) Event.FALL else null
                    }
                }
            },
            mockRepo
        )
        service.setSession(1L)
        repeat(6) { service.processReading(makeReading(it.toDouble())) }

        // first window = null, windows 2-4 = FALL = 3 saved events
        assertEquals(Event.FALL, service.lastEvent.first()?.event)
        assertEquals(3, savedEvents.size)
    }

    @Test
    fun `changing session id mid stream attaches correct id to new events`() = runBlocking {
        val service = ClimbEventService(
            mockStrategy(shortSize = 3, shortResult = Event.REST),
            mockRepo
        )
        service.setSession(1L)
        repeat(3) { service.processReading(makeReading(it.toDouble())) }

        service.setSession(2L)
        repeat(3) { service.processReading(makeReading(it.toDouble())) }

        assertEquals(1L, savedEvents[0].sessionId)
        assertEquals(2L, savedEvents[1].sessionId)
    }

    @Test
    fun `altitude from reading is correctly stored in event`() = runBlocking {
        val service = ClimbEventService(
            mockStrategy(shortSize = 3, shortResult = Event.FALL),
            mockRepo
        )
        service.setSession(1L)
        service.processReading(makeReading(1.0))
        service.processReading(makeReading(2.0))
        service.processReading(makeReading(99.5))

        assertEquals(99.5, savedEvents.first().altitude)
    }
}
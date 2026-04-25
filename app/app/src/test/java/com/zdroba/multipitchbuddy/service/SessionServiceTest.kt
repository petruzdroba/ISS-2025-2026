package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event
import com.zdroba.multipitchbuddy.entity.Session
import com.zdroba.multipitchbuddy.repository.ISessionRepository
import com.zdroba.multipitchbuddy.utils.ILocationProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.Instant
import kotlin.time.Duration.Companion.milliseconds

class SessionServiceTest {

    private val savedSessions = mutableListOf<Session>()
    private val updatedSessions = mutableListOf<Session>()
    private val savedEvents = mutableListOf<ClimbEvent>()
    private val mockReadings = MutableStateFlow<AltitudeReading?>(null)
    private var lastAltitudeOverride: Double? = null
    private var processReadingResult: ClimbEvent? = null

    private val mockAltitudeService = object : IAltitudeRecorderService {
        override val readings: Flow<AltitudeReading?> = mockReadings
        override val lastAltitude: Double? get() = lastAltitudeOverride
        override suspend fun start() {}
        override suspend fun end() {}
    }

    private val mockClimbEventService = object : IClimbEventService {
        override val lastEvent: Flow<ClimbEvent?> = MutableStateFlow(null)
        override suspend fun processReading(reading: AltitudeReading): ClimbEvent? = processReadingResult
        override suspend fun save(event: ClimbEvent) {
            savedEvents.add(event)
        }

        override fun setSession(sessionId: Long) {}
    }

    private val mockSessionRepository = object : ISessionRepository {
        override suspend fun save(entity: Session): Long {
            savedSessions.add(entity)
            return 0L// for sessionId test
        }

        override suspend fun update(entity: Session) {
            updatedSessions.add(entity)
        }

        override suspend fun delete(id: Long) {
        }

        override suspend fun getById(id: Long) = savedSessions.first { it.id == id }
        override suspend fun getAll(): List<Session> {
            return TODO("Provide the return value")
        }
    }

    private val mockLocationProvider = object : ILocationProvider {
        override fun getLocation() = 46.77 to 23.59
    }

    private lateinit var service: SessionService

    @Before
    fun setup() {
        savedSessions.clear()
        updatedSessions.clear()
        savedEvents.clear()
        mockReadings.value = null
        lastAltitudeOverride = null
        processReadingResult = null
        service =
            SessionService(mockLocationProvider, mockAltitudeService, mockClimbEventService, mockSessionRepository)
    }

    @Test
    fun `start saves session to repo`() = runBlocking {
        service.start()
        assertEquals(1, savedSessions.size)
    }

    @Test
    fun `start sets isRecording to true`() = runBlocking {
        service.start()
        assertTrue(service.isRecording.first())
    }

    @Test
    fun `start saves SESSION_STARTED event`() = runBlocking {
        service.start()
        assertTrue(savedEvents.any { it.event == Event.SESSION_STARTED })
    }

    @Test
    fun `start sets currentSession`() = runBlocking {
        service.start()
        assertNotNull(service.currentSession.first())
    }

    @Test
    fun `SESSION_STARTED event has correct sessionId`() = runBlocking {
        service.start()
        val session = savedSessions.first()
        val startEvent = savedEvents.first { it.event == Event.SESSION_STARTED }
        assertEquals(session.id, startEvent.sessionId)
    }

    @Test
    fun `SESSION_STARTED event captures last altitude`() = runBlocking {
        lastAltitudeOverride = 42.0
        service.start()
        val startEvent = savedEvents.first { it.event == Event.SESSION_STARTED }
        assertEquals(42.0, startEvent.altitude)
    }

    @Test
    fun `end saves SESSION_ENDED event`() = runBlocking {
        service.start()
        service.end()
        assertTrue(savedEvents.any { it.event == Event.SESSION_ENDED })
    }

    @Test
    fun `end updates session with end time`() = runBlocking {
        service.start()
        service.end()
        assertEquals(1, updatedSessions.size)
        assertNotNull(updatedSessions.first().end)
    }

    @Test
    fun `end sets isRecording to false`() = runBlocking {
        service.start()
        service.end()
        assertFalse(service.isRecording.first())
    }

    @Test
    fun `end clears currentSession`() = runBlocking {
        service.start()
        service.end()
        assertNull(service.currentSession.first())
    }

    @Test
    fun `end without start does nothing`() = runBlocking {
        service.end()
        assertEquals(0, updatedSessions.size)
        assertEquals(0, savedEvents.size)
    }

    @Test
    fun `end clears events list`() = runBlocking {
        service.start()
        service.end()
        assertEquals(0, service.events.first().size)
    }

    @Test
    fun `altitude reading gets processed and added to events`() = runBlocking {
        val reading = AltitudeReading(5.0, Instant.now())
        val climbEvent = ClimbEvent(time = Instant.now(), event = Event.FALL, sessionId = 0)
        processReadingResult = climbEvent

        service.start()
        mockReadings.value = reading
        delay(100.milliseconds)

        assertEquals(1, service.events.first().size)
        assertEquals(Event.FALL, service.events.first().first().event)
    }

    @Test
    fun `null reading is ignored`() = runBlocking {
        service.start()
        mockReadings.value = null
        delay(100.milliseconds)
        assertEquals(0, service.events.first().size)
    }

    @Test
    fun `session has null coordinates when location unavailable`() = runBlocking {
        val serviceNoLocation = SessionService(
            object : ILocationProvider {
                override fun getLocation() = null
            },
            mockAltitudeService,
            mockClimbEventService,
            mockSessionRepository
        )
        serviceNoLocation.start()
        val session = savedSessions.first()
        assertNull(session.latitude)
        assertNull(session.longitude)
    }

    @Test
    fun `session start time is set on start`() = runBlocking {
        val before = Instant.now()
        service.start()
        val after = Instant.now()
        val session = savedSessions.first()
        assertTrue(session.start in before..after)
    }

    @Test
    fun `end time is set after end`() = runBlocking {
        service.start()
        val before = Instant.now()
        service.end()
        val after = Instant.now()
        val ended = updatedSessions.first()
        assertTrue(ended.end!! in before..after)
    }
}
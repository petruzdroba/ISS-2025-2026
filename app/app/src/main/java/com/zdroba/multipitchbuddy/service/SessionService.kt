package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Event
import com.zdroba.multipitchbuddy.entity.Session
import com.zdroba.multipitchbuddy.repository.ISessionRepository
import com.zdroba.multipitchbuddy.utils.ILocationProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class SessionService(
    private val locationProvider: ILocationProvider,
    private val altitudeService: IAltitudeRecorderService,
    private val climbEventService: IClimbEventService,
    private val sessionRepository: ISessionRepository,
): ISessionService {

    private val _currentSession = MutableStateFlow<Session?>(null)
    override val currentSession: Flow<Session?> = _currentSession.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: Flow<Boolean> = _isRecording.asStateFlow()

    private val _events = MutableStateFlow<List<ClimbEvent>>(emptyList())
    override val events: Flow<List<ClimbEvent>> = _events.asStateFlow()

    private var recordingJob: kotlinx.coroutines.Job? = null

    override suspend fun start() {
        val location = locationProvider.getLocation()

        val session = Session(
            userId = null, // TODO: pass logged in user id if available
            start = Instant.now(),
            latitude = location?.first,
            longitude = location?.second
        )

        sessionRepository.save(session)
        _currentSession.value = session
        _isRecording.value = true
        _events.value = emptyList()

        climbEventService.setSession(session.id)
        climbEventService.save(ClimbEvent(
            time = Instant.now(),
            event = Event.SESSION_STARTED,
            altitude = altitudeService.lastAltitude,
            sessionId = session.id
        ))

        altitudeService.start()

        recordingJob = CoroutineScope(Dispatchers.Default).launch {
            altitudeService.readings.collect { reading ->
                if (reading == null) return@collect
                val event = climbEventService.processReading(reading)
                if (event != null) {
                    _events.value += event
                }
            }
        }
    }

    override suspend fun end() {
        recordingJob?.cancel()
        recordingJob = null

        altitudeService.end()

        val session = _currentSession.value ?: return

        climbEventService.save(ClimbEvent(
            time = Instant.now(),
            event = Event.SESSION_ENDED,
            altitude = altitudeService.lastAltitude,
            sessionId = session.id
        ))

        val ended = session.copy(end = Instant.now())
        sessionRepository.update(ended)

        _currentSession.value = null
        _isRecording.value = false
        _events.value = emptyList()
    }
}
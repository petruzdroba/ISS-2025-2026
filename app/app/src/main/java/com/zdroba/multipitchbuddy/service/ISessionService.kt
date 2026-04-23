package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Session
import kotlinx.coroutines.flow.Flow

interface ISessionService {
    val currentSession:Flow<Session?>
    val isRecording: Flow<Boolean>
    val events: Flow<List<ClimbEvent>>

    suspend fun start()
    suspend fun end()
}
package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent

interface ICrudClimbEventService {

    suspend fun getBySessionId(key: Long): List<ClimbEvent>
}
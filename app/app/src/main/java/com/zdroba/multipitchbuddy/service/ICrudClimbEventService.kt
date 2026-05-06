package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent

interface ICrudClimbEventService: ICrudService<ClimbEvent, Long> {

    suspend fun save(entity: ClimbEvent):Long
    suspend fun getBySessionId(key: Long): List<ClimbEvent>
}
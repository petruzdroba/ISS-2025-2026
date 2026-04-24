package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.repository.IClimbEventRepository

class CrudClimbEventService(
    private val climbEventRepository: IClimbEventRepository,
): ICrudClimbEventService {
    override suspend fun getBySessionId(key: Long): List<ClimbEvent> {
        return climbEventRepository.getBySessionId(key)
    }
}
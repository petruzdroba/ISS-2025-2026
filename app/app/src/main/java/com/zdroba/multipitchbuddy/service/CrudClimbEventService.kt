package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.repository.IClimbEventRepository

class CrudClimbEventService(
    private val climbEventRepository: IClimbEventRepository,
): ICrudClimbEventService {
    override suspend fun getBySessionId(key: Long): List<ClimbEvent> {
        return climbEventRepository.getBySessionId(key)
    }

    override suspend fun delete(key: Long) {
        climbEventRepository.delete(key)
    }

    override suspend fun getAll(): List<ClimbEvent> {
        return climbEventRepository.getAll()
    }

    override suspend fun getById(key: Long): ClimbEvent {
        return climbEventRepository.getById(key)
    }

    override suspend fun update(entity: ClimbEvent) {
        climbEventRepository.update(entity)
    }
}
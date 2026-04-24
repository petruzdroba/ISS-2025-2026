package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.Session
import com.zdroba.multipitchbuddy.repository.ISessionRepository

class CrudSessionService(
    private val sessionRepository: ISessionRepository
):ICrudSessionService {
    override suspend fun delete(key: Long) {
        sessionRepository.delete(key)
    }

    override suspend fun getAll(): List<Session> {
        return sessionRepository.getAll()
    }

    override suspend fun getById(key: Long): Session {
        return sessionRepository.getById(key)
    }

    override suspend fun update(entity: Session) {
        sessionRepository.update(entity)
    }
}
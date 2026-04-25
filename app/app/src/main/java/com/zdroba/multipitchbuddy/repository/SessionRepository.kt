package com.zdroba.multipitchbuddy.repository

import com.zdroba.multipitchbuddy.dao.SessionDao
import com.zdroba.multipitchbuddy.entity.Session
import com.zdroba.multipitchbuddy.exceptions.NotFoundException
import com.zdroba.multipitchbuddy.utils.Logger
import com.zdroba.multipitchbuddy.utils.TimberLogger

class SessionRepository(private val dao: SessionDao, private val logger: Logger = TimberLogger) :
    ISessionRepository {
    override suspend fun save(entity: Session): Long {
        logger.debug("Saved Session {id=%d}", entity.id)
        return dao.insert(entity)
    }

    override suspend fun update(entity: Session) {
        dao.update(entity)
        logger.debug("Updated Session {id=%d}", entity.id)
    }

    override suspend fun delete(id: Long) {
        dao.delete(getById(id))
        logger.debug("Deleted Session {id=%d}", id)
    }

    override suspend fun getById(id: Long): Session {
        return dao.getById(id)?: throw NotFoundException("Session $id not found");
    }

    override suspend fun getAll(): List<Session> {
        return dao.getAll()
    }
}
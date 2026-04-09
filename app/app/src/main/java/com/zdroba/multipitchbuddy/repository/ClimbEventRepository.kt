package com.zdroba.multipitchbuddy.repository

import com.zdroba.multipitchbuddy.dao.ClimbEventDao
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.exceptions.NotFoundException
import com.zdroba.multipitchbuddy.utils.Logger
import com.zdroba.multipitchbuddy.utils.TimberLogger

class ClimbEventRepository(private val dao: ClimbEventDao, private val logger: Logger = TimberLogger) :
    IClimbEventRepository {
    override suspend fun save(entity: ClimbEvent) {
        dao.insert(entity)
        logger.debug("Saved ClimbEvent {id=%d}", entity.id)
    }

    override suspend fun getById(id: Long): ClimbEvent {
        return dao.getById(id)?: throw NotFoundException("ClimbEvent $id not found");
    }
}
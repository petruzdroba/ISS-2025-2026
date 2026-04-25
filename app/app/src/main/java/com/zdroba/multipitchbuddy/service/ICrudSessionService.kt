package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.entity.Session

interface ICrudSessionService {

    suspend fun delete(key:Long)

    suspend fun getAll(): List<Session>

    suspend fun getById(key: Long): Session

    suspend fun update(entity: Session)
}
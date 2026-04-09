package com.zdroba.multipitchbuddy.repository

interface Repository<T,K> {

    suspend fun save(entity: T)

    suspend fun getById(id: K): T;
}
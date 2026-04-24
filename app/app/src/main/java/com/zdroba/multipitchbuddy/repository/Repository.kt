package com.zdroba.multipitchbuddy.repository

interface Repository<T,K> {

    suspend fun save(entity: T): K

    suspend fun update(entity: T)

    suspend fun delete(id: K)

    suspend fun getById(id: K): T

    suspend fun getAll(): List<T>;
}
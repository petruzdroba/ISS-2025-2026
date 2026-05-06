package com.zdroba.multipitchbuddy.service


interface ICrudService<T,K> {

    suspend fun delete(key: K)

    suspend fun getAll(): List<T>

    suspend fun getById(key: K): T

    suspend fun update(entity: T)
}
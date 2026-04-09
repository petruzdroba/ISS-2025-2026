package com.zdroba.multipitchbuddy.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zdroba.multipitchbuddy.entity.ClimbEvent

@Dao
interface ClimbEventDao {

    @Insert
    suspend fun insert(climbEvent: ClimbEvent): Long

    @Update
    suspend fun update(climbEvent: ClimbEvent)

    @Delete
    suspend fun delete(climbEvent: ClimbEvent)

    @Query("SELECT * FROM climb_events WHERE id = :id")
    suspend fun getById(id: Long): ClimbEvent?

    @Query("SELECT * FROM climb_events")
    suspend fun getAll(): List<ClimbEvent>
}
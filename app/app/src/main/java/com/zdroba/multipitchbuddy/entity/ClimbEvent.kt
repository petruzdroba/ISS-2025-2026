package com.zdroba.multipitchbuddy.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "climb_events",
    foreignKeys = [
        ForeignKey(
            entity=Session::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("sessionId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ClimbEvent(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var time: Instant,
    var altitude: Double,
    var event: Event,
    var notes: String,
    var sessionId: Long
) {}
package com.zdroba.multipitchbuddy.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "climb_events")
class ClimbEvent(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var time: Instant,
    var altitude: Double,
    var event: Event,
    var notes: String,
) {}
package com.zdroba.multipitchbuddy.entity

import android.location.Location
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "sessions")
data class Session(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var user: User? = null,
    var start: Instant,
    var end: Instant,
    var name: String,
    var location: Location,
    var events: List<ClimbEvent>
) {}
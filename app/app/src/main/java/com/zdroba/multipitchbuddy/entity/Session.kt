package com.zdroba.multipitchbuddy.entity

import android.location.Location
import java.time.Instant


data class Session(
    var id: Long? = null,
    var user: User? = null,
    var start: Instant,
    var end: Instant,
    var name: String,
    var location: Location,
    var events: List<ClimbEvent>
) {}
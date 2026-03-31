package com.zdroba.multipitchbuddy.entity

import java.time.Instant

class ClimbEvent(
    var id: Long? = null,
    var time: Instant,
    var altitude: Double,
    var event: Event,
    var notes: String,
) {}
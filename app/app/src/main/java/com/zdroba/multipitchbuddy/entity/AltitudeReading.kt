package com.zdroba.multipitchbuddy.entity

import kotlin.time.Instant

data class AltitudeReading(
    var altitude: Double,
    var time: Instant
) {}
package com.zdroba.multipitchbuddy.entity

import java.time.Instant

data class AltitudeReading(
    var altitude: Double,
    var time: Instant
) {}
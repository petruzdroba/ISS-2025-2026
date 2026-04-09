package com.zdroba.multipitchbuddy.service.classification

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.Event

interface ClassificationStrategy {
    val shortWindowSize: Int
    val longWindowSize: Int
    val fallDropThreshold: Double
    val allowedNoiseThreshold: Double
    val restMaxChangeThreshold: Double
    val pitchAltitudeThreshold: Double
    val pitchRestTimeThreshold: Long

    fun classifyShort(readings: List<AltitudeReading>): Event?
    fun classifyLong(readings: List<AltitudeReading>): Event?
}
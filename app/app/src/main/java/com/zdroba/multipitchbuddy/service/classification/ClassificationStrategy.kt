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

    /*
    * Smoothens the readings list -> reduce overall noise of the altitude
    *
    * Each reading gets averaged out with its left and right neighbor
    * Makes the classification based on altitude change much more reliable
    * */
    fun smooth(readings: List<AltitudeReading>): List<AltitudeReading> {
        if (readings.size < 3)
            return readings

        val smoothed = mutableListOf(readings.first())

        for (idx in 1 until readings.size - 1) {
            val average = (readings[idx - 1].altitude + readings[idx].altitude + readings[idx + 1].altitude) / 3
            smoothed.add(readings[idx].copy(altitude = average))
        }

        smoothed.add(readings.last())
        return smoothed
    }

    /*
    * Create upwards or downwards deltas
    *
    * Each reading gets paired with its neighbor -> delta change
    * Used to identify trends in movement like -> slow ascent + fast fall delta -> Event.FALL
    * */
    fun deltas(readings: List<AltitudeReading>): List<Double> {
        return readings.zipWithNext { a, b -> b.altitude - a.altitude }
    }
}
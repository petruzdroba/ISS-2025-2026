package com.zdroba.multipitchbuddy.service.classification

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.Event
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

class BalancedStrategy: ClassificationStrategy {
    override val shortWindowSize: Int = 8
    override val longWindowSize: Int = 60
    override val fallDropThreshold: Double = 0.4
    override val allowedNoiseThreshold: Double = 0.4
    override val restMaxChangeThreshold: Double = 0.25
    override val pitchAltitudeThreshold: Double = 5.0
    override val pitchRestTimeThreshold: Long = 10L

    override fun classifyShort(readings: List<AltitudeReading>): Event? {
        if (readings.size != shortWindowSize) {
            return null
        }

        val sorted = smooth(readings).sortedBy { it.time }
        val deltas = deltas(sorted)

        if (detectRest(sorted)) return Event.REST
        if (detectFall(deltas)) return Event.FALL
        if (detectRetreat(deltas)) return Event.RETREAT

        return null
    }

    override fun classifyLong(readings: List<AltitudeReading>): Event? {
        if (readings.size != longWindowSize)
            return null

        val sorted = smooth(readings).sortedBy { it.time }
        if(detectPitchChange(sorted))
            return Event.PITCH_CHANGED

        return null
    }


    /*
    * If the standard deviation is close to the mean
    *
    * Then it's probably a rest
    * */
    private fun detectRest(readings: List<AltitudeReading>): Boolean {
        val mean = readings.map { it.altitude }.average()
        val stdDev = sqrt(readings.map { (it.altitude - mean).pow(2.0) }.average())

        return stdDev <= restMaxChangeThreshold
    }

    /*
    * Requires an ascent phase and then a drop in the deltas beyond the fall threshold
    * */
    private fun detectFall(deltas: List<Double>): Boolean {
        if (deltas.size < 4)
            return false
        val firstHalf = deltas.take(deltas.size / 2)

        if (firstHalf.none { it > allowedNoiseThreshold / 2 })
            return false

        return deltas.any { it <= -fallDropThreshold }
    }

    /*
    * Detect a control descent in the first 60% of readings
    * deltas should be consistently negative, last 40% should be stabilization
    * */
    private fun detectRetreat(deltas: List<Double>): Boolean {
        if (deltas.size < 4)
            return false

        val declineCount = ceil(deltas.size * 0.6).toInt()
        val declinePhase = deltas.take(declineCount)
        val stabilizationPhase = deltas.drop(declineCount)
        val declineRatio = declinePhase.count { it < -allowedNoiseThreshold / 2 }.toDouble() / declinePhase.size

        if (declineRatio < 0.6)
            return false
        val stabilizationRatio = if (stabilizationPhase.isEmpty()) 1.0
        else stabilizationPhase.count { abs(it) <= allowedNoiseThreshold }.toDouble() / stabilizationPhase.size

        return stabilizationRatio >= 0.7
    }

    /*
    * Detects a pitch change, requires significant altitude gan in the first 60%
    * and in the 40% a stabilization/rest period of at least pitchRestTime Seconds
    * */
    private fun detectPitchChange(readings: List<AltitudeReading>): Boolean {
        if (readings.size < 6)
            return false

        val totalGain = readings.last().altitude - readings.first().altitude
        if (totalGain < pitchAltitudeThreshold)
            return false

        val midPoint = (readings.size * 0.6).toInt()
        val climbPhase = readings.take(midPoint)
        val restPhase = readings.drop(midPoint)
        val climbGain = climbPhase.last().altitude - climbPhase.first().altitude

        if (climbGain / totalGain < 0.7)
            return false
        if (restPhase.size < 3)
            return false
        val restVariation = restPhase.maxOf { it.altitude } - restPhase.minOf { it.altitude }

        if (restVariation > 1.5)
            return false

        val restDuration = restPhase.last().time.toEpochMilli() - restPhase.first().time.toEpochMilli()
        return restDuration / 1000 >= pitchRestTimeThreshold
    }
}
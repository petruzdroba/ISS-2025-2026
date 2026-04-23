package com.zdroba.multipitchbuddy.service.classification

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.Event
import kotlin.math.pow
import kotlin.math.sqrt

class SportStrategy : ClassificationStrategy {
    override val shortWindowSize: Int = 6
    override val longWindowSize: Int = 60 // defined for one pitch
    override val fallDropThreshold: Double = 0.25
    override val allowedNoiseThreshold: Double = 0.3
    override val restMaxChangeThreshold: Double = 0.15
    override val pitchAltitudeThreshold: Double = 5.0
    override val pitchRestTimeThreshold: Long = 8L

    override fun classifyShort(readings: List<AltitudeReading>): Event? {
        if (readings.size != shortWindowSize)
            return null

        val sorted = smooth(readings).sortedBy { it.time }
        val deltas = deltas(sorted)

        if (detectRest(sorted))
            return Event.REST
        if (detectFall(deltas))
            return Event.FALL

        return null
    }

    override fun classifyLong(readings: List<AltitudeReading>): Event? {
        if (readings.size != longWindowSize)
            return null

        val sorted = readings.sortedBy { it.time }
        if (detectClimbCompleted(sorted))
            return Event.CLIMB_COMPLETED

        return null
    }

    private fun detectRest(readings: List<AltitudeReading>): Boolean {
        val mean = readings.map { it.altitude }.average()
        val stdDev = sqrt(readings.map { (it.altitude - mean).pow(2.0) }.average())

        return stdDev <= restMaxChangeThreshold
    }

    /*
    * Changed to treat possible dynamic jumps
    * require sustained drop over at least 2 consecutive deltas to avoid dynamic movement misfires
    *
    * */
    private fun detectFall(deltas: List<Double>): Boolean {
        if (deltas.size < 3)
            return false

        val firstHalf = deltas.take(deltas.size / 2)
        if (firstHalf.none { it > allowedNoiseThreshold / 2 })
            return false

        var consecutiveDrops = 0
        for (delta in deltas) {
            if (delta <= -fallDropThreshold) {
                consecutiveDrops++
                if (consecutiveDrops >= 2)
                    return true
            } else {
                consecutiveDrops = 0
            }
        }

        return false
    }

    private fun detectClimbCompleted(readings: List<AltitudeReading>): Boolean {
        if (readings.size < 6) return false

        val maxAltitude = readings.maxOf { it.altitude }
        val totalGain = maxAltitude - readings.first().altitude
        if (totalGain < pitchAltitudeThreshold) return false

        val midPoint = (readings.size * 0.6).toInt()
        val climbPhase = readings.take(midPoint)
        val restPhase = readings.drop(midPoint)

        val climbGain = climbPhase.maxOf { it.altitude } - climbPhase.first().altitude
        if (climbGain / totalGain < 0.7) return false

        if (restPhase.size < 3) return false

        val initialRest = restPhase.take(4)
        val restVariation = initialRest.maxOf { it.altitude } - initialRest.minOf { it.altitude }
        if (restVariation > 1.0) return false

        val restDuration = restPhase.last().time.toEpochMilli() - restPhase.first().time.toEpochMilli()
        return restDuration / 1000 >= pitchRestTimeThreshold
    }
}
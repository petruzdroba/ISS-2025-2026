package com.zdroba.multipitchbuddy.service.classification

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.Event
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt

class OriginalStrategy : ClassificationStrategy {

    override val shortWindowSize = 8
    override val longWindowSize = 60

    override val fallDropThreshold = 0.4
    override val allowedNoiseThreshold = 0.4
    override val restMaxChangeThreshold = 0.25
    override val pitchAltitudeThreshold = 5.0
    override val pitchRestTimeThreshold = 10L

    override fun classifyShort(readings: List<AltitudeReading>): Event? {
        if (readings.size != shortWindowSize) return null

        val sorted = smooth(readings).sortedBy { it.time }
        val deltas = deltas(sorted)

        return when {
            detectRest(sorted) -> Event.REST
            detectFall(deltas) -> Event.FALL
            detectRetreat(deltas) -> Event.RETREAT
            else -> null
        }
    }

    override fun classifyLong(readings: List<AltitudeReading>): Event? {
        if (readings.size != longWindowSize) return null

        val sorted = smooth(readings).sortedBy { it.time }

        return if (detectPitchChange(sorted)) {
            Event.PITCH_CHANGED
        } else null
    }

    private fun detectRest(readings: List<AltitudeReading>): Boolean {
        val timeSpan = readings.last().time.toEpochMilli() - readings.first().time.toEpochMilli()

        if (timeSpan > 8000) return false

        val mean = readings.map { it.altitude }.average()
        val stdDev = sqrt(
            readings.map { (it.altitude - mean).pow(2) }.average()
        )

        return stdDev <= restMaxChangeThreshold
    }

    private fun detectFall(deltas: List<Double>): Boolean {
        if (deltas.size < 4) return false

        val firstHalf = deltas.take(deltas.size / 2)

        val hasClimb = firstHalf.any { it > allowedNoiseThreshold / 2 }
        if (!hasClimb) return false

        return deltas.any {
            it <= -fallDropThreshold && it <= -allowedNoiseThreshold
        }
    }

    private fun detectRetreat(deltas: List<Double>): Boolean {
        if (deltas.size < 4) return false

        val declineCount = ceil(deltas.size * 0.6).toInt()

        val declinePhase = deltas.take(declineCount)
        val stabilizationPhase = deltas.drop(declineCount)

        val declineRatio =
            declinePhase.count { it < -allowedNoiseThreshold / 2 }
                .toDouble() / declinePhase.size

        if (declineRatio < 0.6) return false

        val stabilizationRatio =
            if (stabilizationPhase.isEmpty()) 1.0
            else stabilizationPhase.count { abs(it) <= allowedNoiseThreshold }
                .toDouble() / stabilizationPhase.size

        return stabilizationRatio >= 0.7
    }

    private fun detectPitchChange(readings: List<AltitudeReading>): Boolean {
        if (readings.size < 6) return false

        val totalGain =
            readings.last().altitude - readings.first().altitude

        if (totalGain < pitchAltitudeThreshold) return false

        val midPoint = (readings.size * 0.6).toInt()

        val climbPhase = readings.take(midPoint)
        val restPhase = readings.drop(midPoint)

        val climbGain =
            climbPhase.last().altitude - climbPhase.first().altitude

        if (climbGain / totalGain < 0.7) return false

        if (restPhase.size < 3) return false

        val restVariation =
            restPhase.maxOf { it.altitude } -
                    restPhase.minOf { it.altitude }

        if (restVariation > 1.5) return false

        val restDuration = restPhase.last().time.toEpochMilli() - restPhase.first().time.toEpochMilli()

        return (restDuration / 1000) >= pitchRestTimeThreshold
    }
}
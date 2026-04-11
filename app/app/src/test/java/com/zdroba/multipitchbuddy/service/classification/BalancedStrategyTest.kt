package com.zdroba.multipitchbuddy.service.classification

import com.zdroba.multipitchbuddy.entity.AltitudeReading
import com.zdroba.multipitchbuddy.entity.Event
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.time.Instant

class BalancedStrategyTest {

    private lateinit var strategy: BalancedStrategy

    @Before
    fun setup() {
        strategy = BalancedStrategy()
    }
    private fun buildReadings(altitudes: List<Double>, intervalMs: Long = 750): List<AltitudeReading> {
        val base = Instant.now()
        return altitudes.mapIndexed { i, alt ->
            AltitudeReading(
                altitude = alt,
                time = base.plusMillis(i * intervalMs)
            )
        }
    }

    @Test
    fun `detectRest returns REST when altitudes are stable`() {
        val readings = buildReadings(listOf(10.0, 10.1, 10.05, 10.08, 10.02, 10.1, 10.03, 10.07))
        Assert.assertEquals(Event.REST, strategy.classifyShort(readings))
    }

    @Test
    fun `detectFall returns FALL when climb followed by drop`() {
        val readings = buildReadings(listOf(10.0, 10.5, 11.0, 11.5, 11.0, 10.5, 9.5, 8.0))
        Assert.assertEquals(Event.FALL, strategy.classifyShort(readings))
    }

    @Test
    fun `detectRetreat returns RETREAT on consistent descent`() {
        val readings = buildReadings(listOf(15.0, 14.5, 14.0, 13.5, 13.0, 12.8, 12.7, 12.6))
        Assert.assertEquals(Event.RETREAT, strategy.classifyShort(readings))
    }

    @Test
    fun `classifyShort returns null when no pattern detected`() {
        val readings = buildReadings(listOf(10.0, 10.6, 10.2, 11.0, 10.4, 11.2, 10.8, 11.4))
        Assert.assertNull(strategy.classifyShort(readings))
    }

    @Test
    fun `classifyShort returns null when wrong window size`() {
        val readings = buildReadings(listOf(10.0, 10.5, 11.0))
        Assert.assertNull(strategy.classifyShort(readings))
    }

    @Test
    fun `detectPitchChange returns PITCH_CHANGED when significant climb followed by rest`() {
        val readings = buildReadings(
            listOf(
                0.0, 1.0, 2.5, 4.0, 5.5, 7.0, 8.5, 10.0, // climb phase
                11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, // still climbing
                19.0, 20.0, 21.0, 22.0, 23.0, 24.0, 25.0, 26.0,
                27.0, 28.0, 29.0, 30.0, 31.0, 32.0, 33.0, 34.0,
                35.0, 36.0, 37.0, 38.0, // approaching anchor
                38.1, 38.05, 38.2, 38.1, // rest phase at anchor
                38.0, 38.15, 38.05, 38.1,
                38.2, 38.0, 38.1, 38.05,
                38.1, 38.2, 38.0, 38.1,
                38.05, 38.1, 38.2, 38.0,
                38.1, 38.05, 38.0, 38.1
            ),
            intervalMs = 750
        )
        Assert.assertEquals(Event.PITCH_CHANGED, strategy.classifyLong(readings))
    }

    @Test
    fun `classifyLong returns null when no pitch change detected`() {
        val readings = buildReadings(
            listOf(
                10.0, 10.3, 10.6, 10.2, 10.8, 10.4, 10.7, 10.1,
                10.5, 10.3, 10.8, 10.2, 10.6, 10.4, 10.7, 10.3,
                10.5, 10.1, 10.8, 10.4, 10.6, 10.2, 10.7, 10.3,
                10.5, 10.8, 10.2, 10.6, 10.4, 10.7, 10.1, 10.5,
                10.3, 10.8, 10.2, 10.6, 10.4, 10.7, 10.3, 10.5,
                10.1, 10.8, 10.4, 10.6, 10.2, 10.7, 10.3, 10.5,
                10.8, 10.2, 10.6, 10.4, 10.7, 10.1, 10.5, 10.3,
                10.8, 10.2, 10.6, 10.4
            ),
            intervalMs = 750
        )
        Assert.assertNull(strategy.classifyLong(readings))
    }
}
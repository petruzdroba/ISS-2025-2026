package com.zdroba.multipitchbuddy

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import android.util.Log
import com.zdroba.multipitchbuddy.service.AltitudeRecorderService
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

// THIS RUNS ON AN ACTUAL ANDROID WITH A BAROMETER
// to see the logs run with
// adb logcat -s AltitudeTest > altitude_test.log
// then run the actual test
// gradle connectedAndroidTest

@RunWith(AndroidJUnit4::class)
class AltitudeRecorderTest {

    @Test
    fun testAltitudeRecording() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val service = AltitudeRecorderService(context)
        val results = mutableListOf<String>()

        service.start()
        Log.d("AltitudeTest", "Started recording")

        // collect readings for 30 seconds
        val job = launch {
            service.readings.collect { reading ->
                if (reading != null) {
                    results.add("altitude=${reading.altitude} time=${reading.time}")
                    Log.d("AltitudeTest", "altitude=${reading.altitude} time=${reading.time}")
                }
            }
        }

        delay(30_000.milliseconds)
        job.cancel()
        service.end()
        Log.d("AltitudeTest", "Stopped recording")

        val file = java.io.File(context.filesDir, "altitude_test.txt")
        file.writeText(results.joinToString("\n"))
    }
}
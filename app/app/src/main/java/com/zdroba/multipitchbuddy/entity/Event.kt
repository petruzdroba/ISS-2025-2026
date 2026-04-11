package com.zdroba.multipitchbuddy.entity

enum class Event {
    SESSION_STARTED,
    SESSION_ENDED,
    FALL,
    REST,
    MANUAL_NOTE,
    ERROR,
    PITCH_CHANGED,
    BAROMETER_READING,
    RETREAT,
    CLIMB_COMPLETED // for sport climbing only instead of PITCH_CHANGED
}
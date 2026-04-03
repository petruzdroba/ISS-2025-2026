package com.zdroba.multipitchbuddy.utils

import androidx.room.TypeConverter
import com.zdroba.multipitchbuddy.entity.Event

class EventConverter {
    @TypeConverter
    fun fromEvent(event: Event?): String? = event?.name

    @TypeConverter
    fun toEvent(value: String?): Event? = value?.let { Event.valueOf(it) }
}
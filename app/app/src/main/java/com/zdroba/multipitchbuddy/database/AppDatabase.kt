package com.zdroba.multipitchbuddy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zdroba.multipitchbuddy.dao.ClimbEventDao
import com.zdroba.multipitchbuddy.dao.SessionDao
import com.zdroba.multipitchbuddy.entity.ClimbEvent
import com.zdroba.multipitchbuddy.entity.Session

@Database(
    entities = [Session::class, ClimbEvent::class],
    version = 1
)
@TypeConverters(
    com.zdroba.multipitchbuddy.utils.InstantConverter::class,
    com.zdroba.multipitchbuddy.utils.EventConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun climbEventDao(): ClimbEventDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "multipitch_buddy.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
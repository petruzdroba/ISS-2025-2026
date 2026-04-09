package com.zdroba.multipitchbuddy.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("userId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Session(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(index = true)
    val userId: Long?, // not logged in null
    var start: Instant,
    var end: Instant? = null,
    var name: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) {}
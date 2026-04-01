package com.zdroba.multipitchbuddy.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var username: String,
    var email: String,
    var password: String
) {}
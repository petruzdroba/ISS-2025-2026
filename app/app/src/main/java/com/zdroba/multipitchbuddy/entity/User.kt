package com.zdroba.multipitchbuddy.entity

data class User(
    var id: Long = 0,
    var username: String,
    var email: String,
    var password: String
) {}
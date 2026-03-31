package com.zdroba.multipitchbuddy.entity

data class User(
    var id: Long? = null,
    var username: String,
    var email: String,
    var password: String
) {}
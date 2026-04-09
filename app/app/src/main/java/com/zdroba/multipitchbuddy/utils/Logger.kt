package com.zdroba.multipitchbuddy.utils

interface Logger {
    fun debug(message: String, vararg args: Any?)
    fun error(message: String, vararg args: Any?)
    fun info(message: String, vararg args: Any?)
}
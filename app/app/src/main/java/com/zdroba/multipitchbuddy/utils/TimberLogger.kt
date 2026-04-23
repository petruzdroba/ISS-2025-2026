package com.zdroba.multipitchbuddy.utils

import timber.log.Timber

object TimberLogger: Logger {
    override fun debug(message: String, vararg args: Any?) {
        Timber.d(message, *args);
    }

    override fun error(message: String, vararg args: Any?) {
        Timber.e(message, *args);
    }

    override fun info(message: String, vararg args: Any?) {
        Timber.i(message, *args)
    }
}
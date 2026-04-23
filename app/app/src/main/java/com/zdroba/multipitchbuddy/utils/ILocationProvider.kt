package com.zdroba.multipitchbuddy.utils

interface ILocationProvider {
    fun getLocation(): Pair<Double, Double>?
}
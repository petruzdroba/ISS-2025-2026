package com.zdroba.multipitchbuddy

import android.app.Application

import com.zdroba.multipitchbuddy.database.AppDatabase
import com.zdroba.multipitchbuddy.repository.ClimbEventRepository
import com.zdroba.multipitchbuddy.repository.SessionRepository
import com.zdroba.multipitchbuddy.service.AltitudeRecorderService
import com.zdroba.multipitchbuddy.service.ClimbEventService
import com.zdroba.multipitchbuddy.service.CrudClimbEventService
import com.zdroba.multipitchbuddy.service.CrudSessionService
import com.zdroba.multipitchbuddy.service.SessionService
import com.zdroba.multipitchbuddy.utils.AndroidLocationProvider
import com.zdroba.multipitchbuddy.service.classification.BalancedStrategy
import com.zdroba.multipitchbuddy.ui.GradientGenerator

class App: Application() {

    val database by lazy { AppDatabase.getInstance(this) }

    val sessionRepository by lazy { SessionRepository(database.sessionDao()) }
    val climbEventRepository by lazy { ClimbEventRepository(database.climbEventDao()) }

    val altitudeService by lazy { AltitudeRecorderService(this) }
    val climbEventService by lazy { ClimbEventService(BalancedStrategy(), climbEventRepository) }
    val locationProvider by lazy { AndroidLocationProvider(this) }
    val sessionService by lazy { SessionService(locationProvider, altitudeService, climbEventService, sessionRepository) }

    val crudClimbEventService by lazy { CrudClimbEventService(climbEventRepository) }
    val crudSessionService by lazy { CrudSessionService(sessionRepository) }
}
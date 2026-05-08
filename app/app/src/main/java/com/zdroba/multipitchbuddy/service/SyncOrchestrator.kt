package com.zdroba.multipitchbuddy.service

import com.zdroba.multipitchbuddy.database.AppDatabase
import com.zdroba.multipitchbuddy.database.LocalDbSyncManager

class SyncOrchestrator(
    private val syncService: SyncService,
    private val localDbSyncManager: LocalDbSyncManager
) {

    suspend fun upload() {
        val dbBytes = localDbSyncManager.exportDb()
        syncService.upload(dbBytes)
    }

    suspend fun download() {
        val response = syncService.download()
        localDbSyncManager.importDb(response)
        AppDatabase.closeInstance()
    }
}
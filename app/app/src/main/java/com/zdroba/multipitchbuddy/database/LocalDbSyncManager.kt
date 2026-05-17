package com.zdroba.multipitchbuddy.database

import android.content.Context
import java.io.File

class LocalDbSyncManager(
    private val context: Context,
    private val database: AppDatabase
) {

    private val dbName = "multipitch_buddy.db"

    fun exportDb(): ByteArray {
        val dbFile = context.getDatabasePath("multipitch_buddy.db")
        return dbFile.readBytes()
    }

    fun importDb(data: ByteArray) {
        AppDatabase.closeInstance()

        val dbFile = context.getDatabasePath(dbName)

        File(dbFile.path + "-wal").delete()
        File(dbFile.path + "-shm").delete()
        dbFile.delete()

        dbFile.writeBytes(data)
    }
}
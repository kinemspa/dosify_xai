package com.xai.dosify.core.utils

import androidx.room.RoomDatabase
import android.content.Context
import java.io.File
import android.os.Environment
import timber.log.Timber

fun backupDatabase(db: RoomDatabase, context: Context) {
    try {
        db.close()
        val dbFile = context.getDatabasePath("dosify.db")
        val backupFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "dosify_backup.db")
        dbFile.copyTo(backupFile, overwrite = true)
        Timber.d("Backup success: ${backupFile.path}")
    } catch (e: Exception) {
        Timber.e(e, "Backup failed")
    }
}

fun restoreDatabase(db: RoomDatabase, context: Context, backupFilePath: String) {
    try {
        db.close()
        val dbFile = context.getDatabasePath("dosify.db")
        val backupFile = File(backupFilePath)
        backupFile.copyTo(dbFile, overwrite = true)
        Timber.d("Restored from ${backupFile.path}")
    } catch (e: Exception) {
        Timber.e(e, "Restore failed")
    }
}
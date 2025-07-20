package com.xai.dosify.core.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add supplies table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS supplies (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                unit TEXT NOT NULL,
                stock REAL NOT NULL,
                lowStockThreshold REAL NOT NULL,
                linkedMedId INTEGER
            )
        """.trimIndent())

        // Add profiles table
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS profiles (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                isPremium INTEGER NOT NULL
            )
        """.trimIndent())
    }
}
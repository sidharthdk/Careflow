package com.careflow.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.careflow.app.data.local.dao.*
import com.careflow.app.data.local.entities.*

@Database(
    entities = [
        CareTaskEntity::class,
        RecoveryEntryEntity::class,
        HealthDocumentEntity::class,
        SymptomEntryEntity::class,
        HealthMeasurementEntity::class,
        NoteEntryEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class CareFlowDatabase : RoomDatabase() {
    abstract fun careTaskDao(): CareTaskDao
    abstract fun recoveryEntryDao(): RecoveryEntryDao
    abstract fun healthDocumentDao(): HealthDocumentDao
    abstract fun symptomEntryDao(): SymptomEntryDao
    abstract fun healthMeasurementDao(): HealthMeasurementDao
    abstract fun noteEntryDao(): NoteEntryDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE health_documents ADD COLUMN processingState TEXT NOT NULL DEFAULT 'CAPTURED'")
                database.execSQL("ALTER TABLE health_documents ADD COLUMN extractedText TEXT")
                database.execSQL("ALTER TABLE health_documents ADD COLUMN processingError TEXT")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS symptom_entries (
                        id TEXT NOT NULL PRIMARY KEY,
                        careEpisodeId TEXT NOT NULL,
                        symptom TEXT NOT NULL,
                        severity TEXT,
                        timestamp TEXT NOT NULL,
                        notes TEXT
                    )"""
                )
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS health_measurements (
                        id TEXT NOT NULL PRIMARY KEY,
                        careEpisodeId TEXT NOT NULL,
                        measurementType TEXT NOT NULL,
                        value TEXT NOT NULL,
                        unit TEXT NOT NULL,
                        timestamp TEXT NOT NULL,
                        notes TEXT
                    )"""
                )
                database.execSQL(
                    """CREATE TABLE IF NOT EXISTS note_entries (
                        id TEXT NOT NULL PRIMARY KEY,
                        careEpisodeId TEXT NOT NULL,
                        content TEXT NOT NULL,
                        timestamp TEXT NOT NULL
                    )"""
                )
            }
        }
    }
}

package com.careflow.app

import android.app.Application
import androidx.room.Room
import com.careflow.app.data.local.database.CareFlowDatabase
import com.careflow.app.data.repository.CareRepositoryImpl
import com.careflow.app.domain.repository.CareRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CareFlowApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: CareFlowDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            CareFlowDatabase::class.java,
            "careflow_database"
        )
            .addMigrations(
                CareFlowDatabase.MIGRATION_1_2,
                CareFlowDatabase.MIGRATION_2_3
            )
            .build()
    }

    val repository: CareRepository by lazy {
        CareRepositoryImpl(
            careTaskDao = database.careTaskDao(),
            recoveryEntryDao = database.recoveryEntryDao(),
            healthDocumentDao = database.healthDocumentDao(),
            symptomEntryDao = database.symptomEntryDao(),
            healthMeasurementDao = database.healthMeasurementDao(),
            noteEntryDao = database.noteEntryDao()
        )
    }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            repository.recoverStaleProcessingDocuments()
            repository.initializeMockData()
        }
    }
}

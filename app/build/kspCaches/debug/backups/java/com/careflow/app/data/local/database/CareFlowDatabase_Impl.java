package com.careflow.app.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import com.careflow.app.data.local.dao.CareTaskDao;
import com.careflow.app.data.local.dao.CareTaskDao_Impl;
import com.careflow.app.data.local.dao.HealthDocumentDao;
import com.careflow.app.data.local.dao.HealthDocumentDao_Impl;
import com.careflow.app.data.local.dao.HealthMeasurementDao;
import com.careflow.app.data.local.dao.HealthMeasurementDao_Impl;
import com.careflow.app.data.local.dao.NoteEntryDao;
import com.careflow.app.data.local.dao.NoteEntryDao_Impl;
import com.careflow.app.data.local.dao.RecoveryEntryDao;
import com.careflow.app.data.local.dao.RecoveryEntryDao_Impl;
import com.careflow.app.data.local.dao.SymptomEntryDao;
import com.careflow.app.data.local.dao.SymptomEntryDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class CareFlowDatabase_Impl extends CareFlowDatabase {
  private volatile CareTaskDao _careTaskDao;

  private volatile RecoveryEntryDao _recoveryEntryDao;

  private volatile HealthDocumentDao _healthDocumentDao;

  private volatile SymptomEntryDao _symptomEntryDao;

  private volatile HealthMeasurementDao _healthMeasurementDao;

  private volatile NoteEntryDao _noteEntryDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `care_tasks` (`id` TEXT NOT NULL, `careEpisodeId` TEXT NOT NULL, `title` TEXT NOT NULL, `description` TEXT NOT NULL, `scheduledTime` TEXT NOT NULL, `isCompleted` INTEGER NOT NULL, `completedAt` TEXT, `taskType` TEXT NOT NULL, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `recovery_entries` (`id` TEXT NOT NULL, `careEpisodeId` TEXT NOT NULL, `timestamp` TEXT NOT NULL, `feeling` TEXT NOT NULL, `symptoms` TEXT NOT NULL, `notes` TEXT, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `health_documents` (`id` TEXT NOT NULL, `careEpisodeId` TEXT, `documentType` TEXT NOT NULL, `title` TEXT NOT NULL, `filePath` TEXT NOT NULL, `capturedAt` TEXT NOT NULL, `isProcessed` INTEGER NOT NULL, `processingState` TEXT NOT NULL, `extractedText` TEXT, `processingError` TEXT, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `symptom_entries` (`id` TEXT NOT NULL, `careEpisodeId` TEXT NOT NULL, `symptom` TEXT NOT NULL, `severity` TEXT, `timestamp` TEXT NOT NULL, `notes` TEXT, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `health_measurements` (`id` TEXT NOT NULL, `careEpisodeId` TEXT NOT NULL, `measurementType` TEXT NOT NULL, `value` TEXT NOT NULL, `unit` TEXT NOT NULL, `timestamp` TEXT NOT NULL, `notes` TEXT, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS `note_entries` (`id` TEXT NOT NULL, `careEpisodeId` TEXT NOT NULL, `content` TEXT NOT NULL, `timestamp` TEXT NOT NULL, PRIMARY KEY(`id`))");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0742f2d975124735598bd4f5e1881eef')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `care_tasks`");
        _db.execSQL("DROP TABLE IF EXISTS `recovery_entries`");
        _db.execSQL("DROP TABLE IF EXISTS `health_documents`");
        _db.execSQL("DROP TABLE IF EXISTS `symptom_entries`");
        _db.execSQL("DROP TABLE IF EXISTS `health_measurements`");
        _db.execSQL("DROP TABLE IF EXISTS `note_entries`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      public void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsCareTasks = new HashMap<String, TableInfo.Column>(8);
        _columnsCareTasks.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCareTasks.put("careEpisodeId", new TableInfo.Column("careEpisodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCareTasks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCareTasks.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCareTasks.put("scheduledTime", new TableInfo.Column("scheduledTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCareTasks.put("isCompleted", new TableInfo.Column("isCompleted", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCareTasks.put("completedAt", new TableInfo.Column("completedAt", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCareTasks.put("taskType", new TableInfo.Column("taskType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCareTasks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCareTasks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoCareTasks = new TableInfo("care_tasks", _columnsCareTasks, _foreignKeysCareTasks, _indicesCareTasks);
        final TableInfo _existingCareTasks = TableInfo.read(_db, "care_tasks");
        if (! _infoCareTasks.equals(_existingCareTasks)) {
          return new RoomOpenHelper.ValidationResult(false, "care_tasks(com.careflow.app.data.local.entities.CareTaskEntity).\n"
                  + " Expected:\n" + _infoCareTasks + "\n"
                  + " Found:\n" + _existingCareTasks);
        }
        final HashMap<String, TableInfo.Column> _columnsRecoveryEntries = new HashMap<String, TableInfo.Column>(6);
        _columnsRecoveryEntries.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryEntries.put("careEpisodeId", new TableInfo.Column("careEpisodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryEntries.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryEntries.put("feeling", new TableInfo.Column("feeling", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryEntries.put("symptoms", new TableInfo.Column("symptoms", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecoveryEntries.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecoveryEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecoveryEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecoveryEntries = new TableInfo("recovery_entries", _columnsRecoveryEntries, _foreignKeysRecoveryEntries, _indicesRecoveryEntries);
        final TableInfo _existingRecoveryEntries = TableInfo.read(_db, "recovery_entries");
        if (! _infoRecoveryEntries.equals(_existingRecoveryEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "recovery_entries(com.careflow.app.data.local.entities.RecoveryEntryEntity).\n"
                  + " Expected:\n" + _infoRecoveryEntries + "\n"
                  + " Found:\n" + _existingRecoveryEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsHealthDocuments = new HashMap<String, TableInfo.Column>(10);
        _columnsHealthDocuments.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("careEpisodeId", new TableInfo.Column("careEpisodeId", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("documentType", new TableInfo.Column("documentType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("filePath", new TableInfo.Column("filePath", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("capturedAt", new TableInfo.Column("capturedAt", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("isProcessed", new TableInfo.Column("isProcessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("processingState", new TableInfo.Column("processingState", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("extractedText", new TableInfo.Column("extractedText", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthDocuments.put("processingError", new TableInfo.Column("processingError", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHealthDocuments = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHealthDocuments = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHealthDocuments = new TableInfo("health_documents", _columnsHealthDocuments, _foreignKeysHealthDocuments, _indicesHealthDocuments);
        final TableInfo _existingHealthDocuments = TableInfo.read(_db, "health_documents");
        if (! _infoHealthDocuments.equals(_existingHealthDocuments)) {
          return new RoomOpenHelper.ValidationResult(false, "health_documents(com.careflow.app.data.local.entities.HealthDocumentEntity).\n"
                  + " Expected:\n" + _infoHealthDocuments + "\n"
                  + " Found:\n" + _existingHealthDocuments);
        }
        final HashMap<String, TableInfo.Column> _columnsSymptomEntries = new HashMap<String, TableInfo.Column>(6);
        _columnsSymptomEntries.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptomEntries.put("careEpisodeId", new TableInfo.Column("careEpisodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptomEntries.put("symptom", new TableInfo.Column("symptom", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptomEntries.put("severity", new TableInfo.Column("severity", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptomEntries.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsSymptomEntries.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysSymptomEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesSymptomEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoSymptomEntries = new TableInfo("symptom_entries", _columnsSymptomEntries, _foreignKeysSymptomEntries, _indicesSymptomEntries);
        final TableInfo _existingSymptomEntries = TableInfo.read(_db, "symptom_entries");
        if (! _infoSymptomEntries.equals(_existingSymptomEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "symptom_entries(com.careflow.app.data.local.entities.SymptomEntryEntity).\n"
                  + " Expected:\n" + _infoSymptomEntries + "\n"
                  + " Found:\n" + _existingSymptomEntries);
        }
        final HashMap<String, TableInfo.Column> _columnsHealthMeasurements = new HashMap<String, TableInfo.Column>(7);
        _columnsHealthMeasurements.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthMeasurements.put("careEpisodeId", new TableInfo.Column("careEpisodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthMeasurements.put("measurementType", new TableInfo.Column("measurementType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthMeasurements.put("value", new TableInfo.Column("value", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthMeasurements.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthMeasurements.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsHealthMeasurements.put("notes", new TableInfo.Column("notes", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysHealthMeasurements = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesHealthMeasurements = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoHealthMeasurements = new TableInfo("health_measurements", _columnsHealthMeasurements, _foreignKeysHealthMeasurements, _indicesHealthMeasurements);
        final TableInfo _existingHealthMeasurements = TableInfo.read(_db, "health_measurements");
        if (! _infoHealthMeasurements.equals(_existingHealthMeasurements)) {
          return new RoomOpenHelper.ValidationResult(false, "health_measurements(com.careflow.app.data.local.entities.HealthMeasurementEntity).\n"
                  + " Expected:\n" + _infoHealthMeasurements + "\n"
                  + " Found:\n" + _existingHealthMeasurements);
        }
        final HashMap<String, TableInfo.Column> _columnsNoteEntries = new HashMap<String, TableInfo.Column>(4);
        _columnsNoteEntries.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNoteEntries.put("careEpisodeId", new TableInfo.Column("careEpisodeId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNoteEntries.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNoteEntries.put("timestamp", new TableInfo.Column("timestamp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNoteEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNoteEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNoteEntries = new TableInfo("note_entries", _columnsNoteEntries, _foreignKeysNoteEntries, _indicesNoteEntries);
        final TableInfo _existingNoteEntries = TableInfo.read(_db, "note_entries");
        if (! _infoNoteEntries.equals(_existingNoteEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "note_entries(com.careflow.app.data.local.entities.NoteEntryEntity).\n"
                  + " Expected:\n" + _infoNoteEntries + "\n"
                  + " Found:\n" + _existingNoteEntries);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0742f2d975124735598bd4f5e1881eef", "1b57f24eab163bd79336f8f2bdac08f8");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "care_tasks","recovery_entries","health_documents","symptom_entries","health_measurements","note_entries");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `care_tasks`");
      _db.execSQL("DELETE FROM `recovery_entries`");
      _db.execSQL("DELETE FROM `health_documents`");
      _db.execSQL("DELETE FROM `symptom_entries`");
      _db.execSQL("DELETE FROM `health_measurements`");
      _db.execSQL("DELETE FROM `note_entries`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(CareTaskDao.class, CareTaskDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecoveryEntryDao.class, RecoveryEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HealthDocumentDao.class, HealthDocumentDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(SymptomEntryDao.class, SymptomEntryDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HealthMeasurementDao.class, HealthMeasurementDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(NoteEntryDao.class, NoteEntryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public CareTaskDao careTaskDao() {
    if (_careTaskDao != null) {
      return _careTaskDao;
    } else {
      synchronized(this) {
        if(_careTaskDao == null) {
          _careTaskDao = new CareTaskDao_Impl(this);
        }
        return _careTaskDao;
      }
    }
  }

  @Override
  public RecoveryEntryDao recoveryEntryDao() {
    if (_recoveryEntryDao != null) {
      return _recoveryEntryDao;
    } else {
      synchronized(this) {
        if(_recoveryEntryDao == null) {
          _recoveryEntryDao = new RecoveryEntryDao_Impl(this);
        }
        return _recoveryEntryDao;
      }
    }
  }

  @Override
  public HealthDocumentDao healthDocumentDao() {
    if (_healthDocumentDao != null) {
      return _healthDocumentDao;
    } else {
      synchronized(this) {
        if(_healthDocumentDao == null) {
          _healthDocumentDao = new HealthDocumentDao_Impl(this);
        }
        return _healthDocumentDao;
      }
    }
  }

  @Override
  public SymptomEntryDao symptomEntryDao() {
    if (_symptomEntryDao != null) {
      return _symptomEntryDao;
    } else {
      synchronized(this) {
        if(_symptomEntryDao == null) {
          _symptomEntryDao = new SymptomEntryDao_Impl(this);
        }
        return _symptomEntryDao;
      }
    }
  }

  @Override
  public HealthMeasurementDao healthMeasurementDao() {
    if (_healthMeasurementDao != null) {
      return _healthMeasurementDao;
    } else {
      synchronized(this) {
        if(_healthMeasurementDao == null) {
          _healthMeasurementDao = new HealthMeasurementDao_Impl(this);
        }
        return _healthMeasurementDao;
      }
    }
  }

  @Override
  public NoteEntryDao noteEntryDao() {
    if (_noteEntryDao != null) {
      return _noteEntryDao;
    } else {
      synchronized(this) {
        if(_noteEntryDao == null) {
          _noteEntryDao = new NoteEntryDao_Impl(this);
        }
        return _noteEntryDao;
      }
    }
  }
}

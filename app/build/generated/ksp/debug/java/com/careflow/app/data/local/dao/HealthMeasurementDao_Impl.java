package com.careflow.app.data.local.dao;

import android.database.Cursor;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.careflow.app.data.local.entities.HealthMeasurementEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HealthMeasurementDao_Impl implements HealthMeasurementDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HealthMeasurementEntity> __insertionAdapterOfHealthMeasurementEntity;

  private final EntityDeletionOrUpdateAdapter<HealthMeasurementEntity> __deletionAdapterOfHealthMeasurementEntity;

  public HealthMeasurementDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHealthMeasurementEntity = new EntityInsertionAdapter<HealthMeasurementEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `health_measurements` (`id`,`careEpisodeId`,`measurementType`,`value`,`unit`,`timestamp`,`notes`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, HealthMeasurementEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
        if (value.getCareEpisodeId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getCareEpisodeId());
        }
        if (value.getMeasurementType() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getMeasurementType());
        }
        if (value.getValue() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getValue());
        }
        if (value.getUnit() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUnit());
        }
        if (value.getTimestamp() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getTimestamp());
        }
        if (value.getNotes() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getNotes());
        }
      }
    };
    this.__deletionAdapterOfHealthMeasurementEntity = new EntityDeletionOrUpdateAdapter<HealthMeasurementEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `health_measurements` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, HealthMeasurementEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
      }
    };
  }

  @Override
  public Object insertEntry(final HealthMeasurementEntity entry,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHealthMeasurementEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteEntry(final HealthMeasurementEntity entry,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfHealthMeasurementEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<HealthMeasurementEntity>> getEntriesForEpisode(final String episodeId) {
    final String _sql = "SELECT * FROM health_measurements WHERE careEpisodeId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (episodeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"health_measurements"}, new Callable<List<HealthMeasurementEntity>>() {
      @Override
      public List<HealthMeasurementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfMeasurementType = CursorUtil.getColumnIndexOrThrow(_cursor, "measurementType");
          final int _cursorIndexOfValue = CursorUtil.getColumnIndexOrThrow(_cursor, "value");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<HealthMeasurementEntity> _result = new ArrayList<HealthMeasurementEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final HealthMeasurementEntity _item;
            final String _tmpId;
            if (_cursor.isNull(_cursorIndexOfId)) {
              _tmpId = null;
            } else {
              _tmpId = _cursor.getString(_cursorIndexOfId);
            }
            final String _tmpCareEpisodeId;
            if (_cursor.isNull(_cursorIndexOfCareEpisodeId)) {
              _tmpCareEpisodeId = null;
            } else {
              _tmpCareEpisodeId = _cursor.getString(_cursorIndexOfCareEpisodeId);
            }
            final String _tmpMeasurementType;
            if (_cursor.isNull(_cursorIndexOfMeasurementType)) {
              _tmpMeasurementType = null;
            } else {
              _tmpMeasurementType = _cursor.getString(_cursorIndexOfMeasurementType);
            }
            final String _tmpValue;
            if (_cursor.isNull(_cursorIndexOfValue)) {
              _tmpValue = null;
            } else {
              _tmpValue = _cursor.getString(_cursorIndexOfValue);
            }
            final String _tmpUnit;
            if (_cursor.isNull(_cursorIndexOfUnit)) {
              _tmpUnit = null;
            } else {
              _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            }
            final String _tmpTimestamp;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmpTimestamp = null;
            } else {
              _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
            }
            final String _tmpNotes;
            if (_cursor.isNull(_cursorIndexOfNotes)) {
              _tmpNotes = null;
            } else {
              _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            }
            _item = new HealthMeasurementEntity(_tmpId,_tmpCareEpisodeId,_tmpMeasurementType,_tmpValue,_tmpUnit,_tmpTimestamp,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

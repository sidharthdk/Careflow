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
import com.careflow.app.data.local.entities.SymptomEntryEntity;
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
public final class SymptomEntryDao_Impl implements SymptomEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SymptomEntryEntity> __insertionAdapterOfSymptomEntryEntity;

  private final EntityDeletionOrUpdateAdapter<SymptomEntryEntity> __deletionAdapterOfSymptomEntryEntity;

  public SymptomEntryDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSymptomEntryEntity = new EntityInsertionAdapter<SymptomEntryEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `symptom_entries` (`id`,`careEpisodeId`,`symptom`,`severity`,`timestamp`,`notes`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SymptomEntryEntity value) {
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
        if (value.getSymptom() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getSymptom());
        }
        if (value.getSeverity() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getSeverity());
        }
        if (value.getTimestamp() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getTimestamp());
        }
        if (value.getNotes() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getNotes());
        }
      }
    };
    this.__deletionAdapterOfSymptomEntryEntity = new EntityDeletionOrUpdateAdapter<SymptomEntryEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `symptom_entries` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, SymptomEntryEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
      }
    };
  }

  @Override
  public Object insertEntry(final SymptomEntryEntity entry,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSymptomEntryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteEntry(final SymptomEntryEntity entry,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfSymptomEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<SymptomEntryEntity>> getEntriesForEpisode(final String episodeId) {
    final String _sql = "SELECT * FROM symptom_entries WHERE careEpisodeId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (episodeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"symptom_entries"}, new Callable<List<SymptomEntryEntity>>() {
      @Override
      public List<SymptomEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfSymptom = CursorUtil.getColumnIndexOrThrow(_cursor, "symptom");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<SymptomEntryEntity> _result = new ArrayList<SymptomEntryEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final SymptomEntryEntity _item;
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
            final String _tmpSymptom;
            if (_cursor.isNull(_cursorIndexOfSymptom)) {
              _tmpSymptom = null;
            } else {
              _tmpSymptom = _cursor.getString(_cursorIndexOfSymptom);
            }
            final String _tmpSeverity;
            if (_cursor.isNull(_cursorIndexOfSeverity)) {
              _tmpSeverity = null;
            } else {
              _tmpSeverity = _cursor.getString(_cursorIndexOfSeverity);
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
            _item = new SymptomEntryEntity(_tmpId,_tmpCareEpisodeId,_tmpSymptom,_tmpSeverity,_tmpTimestamp,_tmpNotes);
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

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
import com.careflow.app.data.local.entities.NoteEntryEntity;
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
public final class NoteEntryDao_Impl implements NoteEntryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NoteEntryEntity> __insertionAdapterOfNoteEntryEntity;

  private final EntityDeletionOrUpdateAdapter<NoteEntryEntity> __deletionAdapterOfNoteEntryEntity;

  public NoteEntryDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNoteEntryEntity = new EntityInsertionAdapter<NoteEntryEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `note_entries` (`id`,`careEpisodeId`,`content`,`timestamp`) VALUES (?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NoteEntryEntity value) {
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
        if (value.getContent() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getContent());
        }
        if (value.getTimestamp() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTimestamp());
        }
      }
    };
    this.__deletionAdapterOfNoteEntryEntity = new EntityDeletionOrUpdateAdapter<NoteEntryEntity>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `note_entries` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NoteEntryEntity value) {
        if (value.getId() == null) {
          stmt.bindNull(1);
        } else {
          stmt.bindString(1, value.getId());
        }
      }
    };
  }

  @Override
  public Object insertEntry(final NoteEntryEntity entry,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfNoteEntryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteEntry(final NoteEntryEntity entry,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfNoteEntryEntity.handle(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<NoteEntryEntity>> getEntriesForEpisode(final String episodeId) {
    final String _sql = "SELECT * FROM note_entries WHERE careEpisodeId = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (episodeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"note_entries"}, new Callable<List<NoteEntryEntity>>() {
      @Override
      public List<NoteEntryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<NoteEntryEntity> _result = new ArrayList<NoteEntryEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final NoteEntryEntity _item;
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
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final String _tmpTimestamp;
            if (_cursor.isNull(_cursorIndexOfTimestamp)) {
              _tmpTimestamp = null;
            } else {
              _tmpTimestamp = _cursor.getString(_cursorIndexOfTimestamp);
            }
            _item = new NoteEntryEntity(_tmpId,_tmpCareEpisodeId,_tmpContent,_tmpTimestamp);
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

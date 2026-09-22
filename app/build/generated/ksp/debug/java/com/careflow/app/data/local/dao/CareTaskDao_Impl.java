package com.careflow.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.careflow.app.data.local.entities.CareTaskEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
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
public final class CareTaskDao_Impl implements CareTaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<CareTaskEntity> __insertionAdapterOfCareTaskEntity;

  private final EntityDeletionOrUpdateAdapter<CareTaskEntity> __updateAdapterOfCareTaskEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteTask;

  public CareTaskDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCareTaskEntity = new EntityInsertionAdapter<CareTaskEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `care_tasks` (`id`,`careEpisodeId`,`title`,`description`,`scheduledTime`,`isCompleted`,`completedAt`,`taskType`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CareTaskEntity value) {
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
        if (value.getTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitle());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        if (value.getScheduledTime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getScheduledTime());
        }
        final int _tmp = value.isCompleted() ? 1 : 0;
        stmt.bindLong(6, _tmp);
        if (value.getCompletedAt() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getCompletedAt());
        }
        if (value.getTaskType() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getTaskType());
        }
      }
    };
    this.__updateAdapterOfCareTaskEntity = new EntityDeletionOrUpdateAdapter<CareTaskEntity>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `care_tasks` SET `id` = ?,`careEpisodeId` = ?,`title` = ?,`description` = ?,`scheduledTime` = ?,`isCompleted` = ?,`completedAt` = ?,`taskType` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, CareTaskEntity value) {
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
        if (value.getTitle() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getTitle());
        }
        if (value.getDescription() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getDescription());
        }
        if (value.getScheduledTime() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getScheduledTime());
        }
        final int _tmp = value.isCompleted() ? 1 : 0;
        stmt.bindLong(6, _tmp);
        if (value.getCompletedAt() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getCompletedAt());
        }
        if (value.getTaskType() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getTaskType());
        }
        if (value.getId() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getId());
        }
      }
    };
    this.__preparedStmtOfDeleteTask = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM care_tasks WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertTask(final CareTaskEntity task,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCareTaskEntity.insert(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object insertTasks(final List<CareTaskEntity> tasks,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfCareTaskEntity.insert(tasks);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateTask(final CareTaskEntity task,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCareTaskEntity.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteTask(final String taskId, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteTask.acquire();
        int _argIndex = 1;
        if (taskId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, taskId);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteTask.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<CareTaskEntity>> getTasksForEpisode(final String episodeId) {
    final String _sql = "SELECT * FROM care_tasks WHERE careEpisodeId = ? ORDER BY scheduledTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (episodeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"care_tasks"}, new Callable<List<CareTaskEntity>>() {
      @Override
      public List<CareTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final List<CareTaskEntity> _result = new ArrayList<CareTaskEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final CareTaskEntity _item;
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
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpScheduledTime;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmpScheduledTime = null;
            } else {
              _tmpScheduledTime = _cursor.getString(_cursorIndexOfScheduledTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getString(_cursorIndexOfCompletedAt);
            }
            final String _tmpTaskType;
            if (_cursor.isNull(_cursorIndexOfTaskType)) {
              _tmpTaskType = null;
            } else {
              _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            }
            _item = new CareTaskEntity(_tmpId,_tmpCareEpisodeId,_tmpTitle,_tmpDescription,_tmpScheduledTime,_tmpIsCompleted,_tmpCompletedAt,_tmpTaskType);
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

  @Override
  public Flow<List<CareTaskEntity>> getTasksForDate(final String episodeId, final String date) {
    final String _sql = "SELECT * FROM care_tasks WHERE careEpisodeId = ? AND DATE(scheduledTime) = DATE(?) ORDER BY scheduledTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    if (episodeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeId);
    }
    _argIndex = 2;
    if (date == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, date);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"care_tasks"}, new Callable<List<CareTaskEntity>>() {
      @Override
      public List<CareTaskEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfScheduledTime = CursorUtil.getColumnIndexOrThrow(_cursor, "scheduledTime");
          final int _cursorIndexOfIsCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "isCompleted");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfTaskType = CursorUtil.getColumnIndexOrThrow(_cursor, "taskType");
          final List<CareTaskEntity> _result = new ArrayList<CareTaskEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final CareTaskEntity _item;
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
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpScheduledTime;
            if (_cursor.isNull(_cursorIndexOfScheduledTime)) {
              _tmpScheduledTime = null;
            } else {
              _tmpScheduledTime = _cursor.getString(_cursorIndexOfScheduledTime);
            }
            final boolean _tmpIsCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsCompleted);
            _tmpIsCompleted = _tmp != 0;
            final String _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getString(_cursorIndexOfCompletedAt);
            }
            final String _tmpTaskType;
            if (_cursor.isNull(_cursorIndexOfTaskType)) {
              _tmpTaskType = null;
            } else {
              _tmpTaskType = _cursor.getString(_cursorIndexOfTaskType);
            }
            _item = new CareTaskEntity(_tmpId,_tmpCareEpisodeId,_tmpTitle,_tmpDescription,_tmpScheduledTime,_tmpIsCompleted,_tmpCompletedAt,_tmpTaskType);
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

  @Override
  public Object countTasksForEpisode(final String episodeId,
      final Continuation<? super Integer> continuation) {
    final String _sql = "SELECT COUNT(*) FROM care_tasks WHERE careEpisodeId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (episodeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if(_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

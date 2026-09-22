package com.careflow.app.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.careflow.app.data.local.entities.HealthDocumentEntity;
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
public final class HealthDocumentDao_Impl implements HealthDocumentDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HealthDocumentEntity> __insertionAdapterOfHealthDocumentEntity;

  private final SharedSQLiteStatement __preparedStmtOfUpdateProcessingResult;

  private final SharedSQLiteStatement __preparedStmtOfDeleteDocument;

  public HealthDocumentDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHealthDocumentEntity = new EntityInsertionAdapter<HealthDocumentEntity>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `health_documents` (`id`,`careEpisodeId`,`documentType`,`title`,`filePath`,`capturedAt`,`isProcessed`,`processingState`,`extractedText`,`processingError`) VALUES (?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, HealthDocumentEntity value) {
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
        if (value.getDocumentType() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getDocumentType());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getTitle());
        }
        if (value.getFilePath() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getFilePath());
        }
        if (value.getCapturedAt() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getCapturedAt());
        }
        final int _tmp = value.isProcessed() ? 1 : 0;
        stmt.bindLong(7, _tmp);
        if (value.getProcessingState() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getProcessingState());
        }
        if (value.getExtractedText() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getExtractedText());
        }
        if (value.getProcessingError() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getProcessingError());
        }
      }
    };
    this.__preparedStmtOfUpdateProcessingResult = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "UPDATE health_documents SET processingState = ?, extractedText = ?, processingError = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteDocument = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM health_documents WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertDocument(final HealthDocumentEntity document,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHealthDocumentEntity.insert(document);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object updateProcessingResult(final String documentId, final String state,
      final String text, final String error, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateProcessingResult.acquire();
        int _argIndex = 1;
        if (state == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, state);
        }
        _argIndex = 2;
        if (text == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, text);
        }
        _argIndex = 3;
        if (error == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, error);
        }
        _argIndex = 4;
        if (documentId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, documentId);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfUpdateProcessingResult.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteDocument(final String documentId,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteDocument.acquire();
        int _argIndex = 1;
        if (documentId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, documentId);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteDocument.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<HealthDocumentEntity>> getAllDocuments() {
    final String _sql = "SELECT * FROM health_documents ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"health_documents"}, new Callable<List<HealthDocumentEntity>>() {
      @Override
      public List<HealthDocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfDocumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "documentType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfProcessingState = CursorUtil.getColumnIndexOrThrow(_cursor, "processingState");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extractedText");
          final int _cursorIndexOfProcessingError = CursorUtil.getColumnIndexOrThrow(_cursor, "processingError");
          final List<HealthDocumentEntity> _result = new ArrayList<HealthDocumentEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final HealthDocumentEntity _item;
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
            final String _tmpDocumentType;
            if (_cursor.isNull(_cursorIndexOfDocumentType)) {
              _tmpDocumentType = null;
            } else {
              _tmpDocumentType = _cursor.getString(_cursorIndexOfDocumentType);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpCapturedAt;
            if (_cursor.isNull(_cursorIndexOfCapturedAt)) {
              _tmpCapturedAt = null;
            } else {
              _tmpCapturedAt = _cursor.getString(_cursorIndexOfCapturedAt);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpProcessingState;
            if (_cursor.isNull(_cursorIndexOfProcessingState)) {
              _tmpProcessingState = null;
            } else {
              _tmpProcessingState = _cursor.getString(_cursorIndexOfProcessingState);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final String _tmpProcessingError;
            if (_cursor.isNull(_cursorIndexOfProcessingError)) {
              _tmpProcessingError = null;
            } else {
              _tmpProcessingError = _cursor.getString(_cursorIndexOfProcessingError);
            }
            _item = new HealthDocumentEntity(_tmpId,_tmpCareEpisodeId,_tmpDocumentType,_tmpTitle,_tmpFilePath,_tmpCapturedAt,_tmpIsProcessed,_tmpProcessingState,_tmpExtractedText,_tmpProcessingError);
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
  public Flow<List<HealthDocumentEntity>> getDocumentsForEpisode(final String episodeId) {
    final String _sql = "SELECT * FROM health_documents WHERE careEpisodeId = ? ORDER BY capturedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (episodeId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, episodeId);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[]{"health_documents"}, new Callable<List<HealthDocumentEntity>>() {
      @Override
      public List<HealthDocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfDocumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "documentType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfProcessingState = CursorUtil.getColumnIndexOrThrow(_cursor, "processingState");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extractedText");
          final int _cursorIndexOfProcessingError = CursorUtil.getColumnIndexOrThrow(_cursor, "processingError");
          final List<HealthDocumentEntity> _result = new ArrayList<HealthDocumentEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final HealthDocumentEntity _item;
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
            final String _tmpDocumentType;
            if (_cursor.isNull(_cursorIndexOfDocumentType)) {
              _tmpDocumentType = null;
            } else {
              _tmpDocumentType = _cursor.getString(_cursorIndexOfDocumentType);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpCapturedAt;
            if (_cursor.isNull(_cursorIndexOfCapturedAt)) {
              _tmpCapturedAt = null;
            } else {
              _tmpCapturedAt = _cursor.getString(_cursorIndexOfCapturedAt);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpProcessingState;
            if (_cursor.isNull(_cursorIndexOfProcessingState)) {
              _tmpProcessingState = null;
            } else {
              _tmpProcessingState = _cursor.getString(_cursorIndexOfProcessingState);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final String _tmpProcessingError;
            if (_cursor.isNull(_cursorIndexOfProcessingError)) {
              _tmpProcessingError = null;
            } else {
              _tmpProcessingError = _cursor.getString(_cursorIndexOfProcessingError);
            }
            _item = new HealthDocumentEntity(_tmpId,_tmpCareEpisodeId,_tmpDocumentType,_tmpTitle,_tmpFilePath,_tmpCapturedAt,_tmpIsProcessed,_tmpProcessingState,_tmpExtractedText,_tmpProcessingError);
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
  public Object getDocumentById(final String documentId,
      final Continuation<? super HealthDocumentEntity> continuation) {
    final String _sql = "SELECT * FROM health_documents WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (documentId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, documentId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<HealthDocumentEntity>() {
      @Override
      public HealthDocumentEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfDocumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "documentType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfProcessingState = CursorUtil.getColumnIndexOrThrow(_cursor, "processingState");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extractedText");
          final int _cursorIndexOfProcessingError = CursorUtil.getColumnIndexOrThrow(_cursor, "processingError");
          final HealthDocumentEntity _result;
          if(_cursor.moveToFirst()) {
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
            final String _tmpDocumentType;
            if (_cursor.isNull(_cursorIndexOfDocumentType)) {
              _tmpDocumentType = null;
            } else {
              _tmpDocumentType = _cursor.getString(_cursorIndexOfDocumentType);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpCapturedAt;
            if (_cursor.isNull(_cursorIndexOfCapturedAt)) {
              _tmpCapturedAt = null;
            } else {
              _tmpCapturedAt = _cursor.getString(_cursorIndexOfCapturedAt);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpProcessingState;
            if (_cursor.isNull(_cursorIndexOfProcessingState)) {
              _tmpProcessingState = null;
            } else {
              _tmpProcessingState = _cursor.getString(_cursorIndexOfProcessingState);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final String _tmpProcessingError;
            if (_cursor.isNull(_cursorIndexOfProcessingError)) {
              _tmpProcessingError = null;
            } else {
              _tmpProcessingError = _cursor.getString(_cursorIndexOfProcessingError);
            }
            _result = new HealthDocumentEntity(_tmpId,_tmpCareEpisodeId,_tmpDocumentType,_tmpTitle,_tmpFilePath,_tmpCapturedAt,_tmpIsProcessed,_tmpProcessingState,_tmpExtractedText,_tmpProcessingError);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getDocumentsByState(final String state,
      final Continuation<? super List<HealthDocumentEntity>> continuation) {
    final String _sql = "SELECT * FROM health_documents WHERE processingState = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (state == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, state);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<HealthDocumentEntity>>() {
      @Override
      public List<HealthDocumentEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCareEpisodeId = CursorUtil.getColumnIndexOrThrow(_cursor, "careEpisodeId");
          final int _cursorIndexOfDocumentType = CursorUtil.getColumnIndexOrThrow(_cursor, "documentType");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfCapturedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "capturedAt");
          final int _cursorIndexOfIsProcessed = CursorUtil.getColumnIndexOrThrow(_cursor, "isProcessed");
          final int _cursorIndexOfProcessingState = CursorUtil.getColumnIndexOrThrow(_cursor, "processingState");
          final int _cursorIndexOfExtractedText = CursorUtil.getColumnIndexOrThrow(_cursor, "extractedText");
          final int _cursorIndexOfProcessingError = CursorUtil.getColumnIndexOrThrow(_cursor, "processingError");
          final List<HealthDocumentEntity> _result = new ArrayList<HealthDocumentEntity>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final HealthDocumentEntity _item;
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
            final String _tmpDocumentType;
            if (_cursor.isNull(_cursorIndexOfDocumentType)) {
              _tmpDocumentType = null;
            } else {
              _tmpDocumentType = _cursor.getString(_cursorIndexOfDocumentType);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpCapturedAt;
            if (_cursor.isNull(_cursorIndexOfCapturedAt)) {
              _tmpCapturedAt = null;
            } else {
              _tmpCapturedAt = _cursor.getString(_cursorIndexOfCapturedAt);
            }
            final boolean _tmpIsProcessed;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsProcessed);
            _tmpIsProcessed = _tmp != 0;
            final String _tmpProcessingState;
            if (_cursor.isNull(_cursorIndexOfProcessingState)) {
              _tmpProcessingState = null;
            } else {
              _tmpProcessingState = _cursor.getString(_cursorIndexOfProcessingState);
            }
            final String _tmpExtractedText;
            if (_cursor.isNull(_cursorIndexOfExtractedText)) {
              _tmpExtractedText = null;
            } else {
              _tmpExtractedText = _cursor.getString(_cursorIndexOfExtractedText);
            }
            final String _tmpProcessingError;
            if (_cursor.isNull(_cursorIndexOfProcessingError)) {
              _tmpProcessingError = null;
            } else {
              _tmpProcessingError = _cursor.getString(_cursorIndexOfProcessingError);
            }
            _item = new HealthDocumentEntity(_tmpId,_tmpCareEpisodeId,_tmpDocumentType,_tmpTitle,_tmpFilePath,_tmpCapturedAt,_tmpIsProcessed,_tmpProcessingState,_tmpExtractedText,_tmpProcessingError);
            _result.add(_item);
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

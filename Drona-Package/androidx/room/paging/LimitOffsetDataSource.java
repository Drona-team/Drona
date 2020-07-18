package androidx.room.paging;

import android.database.Cursor;
import androidx.annotation.RestrictTo;
import androidx.paging.PositionalDataSource;
import androidx.paging.PositionalDataSource.LoadInitialCallback;
import androidx.paging.PositionalDataSource.LoadInitialParams;
import androidx.paging.PositionalDataSource.LoadRangeCallback;
import androidx.paging.PositionalDataSource.LoadRangeParams;
import androidx.room.InvalidationTracker;
import androidx.room.InvalidationTracker.Observer;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.sqlite.wiki.SupportSQLiteQuery;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public abstract class LimitOffsetDataSource<T>
  extends PositionalDataSource<T>
{
  private final String mCountQuery;
  private final RoomDatabase mDatabase;
  private final boolean mInTransaction;
  private final String mLimitOffsetQuery;
  private final InvalidationTracker.Observer mObserver;
  private final RoomSQLiteQuery mSourceQuery;
  
  protected LimitOffsetDataSource(RoomDatabase paramRoomDatabase, RoomSQLiteQuery paramRoomSQLiteQuery, boolean paramBoolean, String... paramVarArgs)
  {
    mDatabase = paramRoomDatabase;
    mSourceQuery = paramRoomSQLiteQuery;
    mInTransaction = paramBoolean;
    paramRoomSQLiteQuery = new StringBuilder();
    paramRoomSQLiteQuery.append("SELECT COUNT(*) FROM ( ");
    paramRoomSQLiteQuery.append(mSourceQuery.getSql());
    paramRoomSQLiteQuery.append(" )");
    mCountQuery = paramRoomSQLiteQuery.toString();
    paramRoomSQLiteQuery = new StringBuilder();
    paramRoomSQLiteQuery.append("SELECT * FROM ( ");
    paramRoomSQLiteQuery.append(mSourceQuery.getSql());
    paramRoomSQLiteQuery.append(" ) LIMIT ? OFFSET ?");
    mLimitOffsetQuery = paramRoomSQLiteQuery.toString();
    mObserver = new InvalidationTracker.Observer(paramVarArgs)
    {
      public void onInvalidated(Set paramAnonymousSet)
      {
        invalidate();
      }
    };
    paramRoomDatabase.getInvalidationTracker().addWeakObserver(mObserver);
  }
  
  protected LimitOffsetDataSource(RoomDatabase paramRoomDatabase, SupportSQLiteQuery paramSupportSQLiteQuery, boolean paramBoolean, String... paramVarArgs)
  {
    this(paramRoomDatabase, RoomSQLiteQuery.copyFrom(paramSupportSQLiteQuery), paramBoolean, paramVarArgs);
  }
  
  private RoomSQLiteQuery getSQLiteQuery(int paramInt1, int paramInt2)
  {
    RoomSQLiteQuery localRoomSQLiteQuery = RoomSQLiteQuery.acquire(mLimitOffsetQuery, mSourceQuery.getArgCount() + 2);
    localRoomSQLiteQuery.copyArgumentsFrom(mSourceQuery);
    localRoomSQLiteQuery.bindLong(localRoomSQLiteQuery.getArgCount() - 1, paramInt2);
    localRoomSQLiteQuery.bindLong(localRoomSQLiteQuery.getArgCount(), paramInt1);
    return localRoomSQLiteQuery;
  }
  
  protected abstract List convertRows(Cursor paramCursor);
  
  public int countItems()
  {
    RoomSQLiteQuery localRoomSQLiteQuery = RoomSQLiteQuery.acquire(mCountQuery, mSourceQuery.getArgCount());
    localRoomSQLiteQuery.copyArgumentsFrom(mSourceQuery);
    Cursor localCursor = mDatabase.query(localRoomSQLiteQuery);
    try
    {
      boolean bool = localCursor.moveToFirst();
      if (bool)
      {
        int i = localCursor.getInt(0);
        localCursor.close();
        localRoomSQLiteQuery.release();
        return i;
      }
      localCursor.close();
      localRoomSQLiteQuery.release();
      return 0;
    }
    catch (Throwable localThrowable)
    {
      localCursor.close();
      localRoomSQLiteQuery.release();
      throw localThrowable;
    }
  }
  
  public boolean isInvalid()
  {
    mDatabase.getInvalidationTracker().refreshVersionsSync();
    return super.isInvalid();
  }
  
  public void loadInitial(PositionalDataSource.LoadInitialParams paramLoadInitialParams, PositionalDataSource.LoadInitialCallback paramLoadInitialCallback)
  {
    List localList2 = Collections.emptyList();
    mDatabase.beginTransaction();
    List localList1 = null;
    Object localObject = null;
    try
    {
      int j = countItems();
      int i;
      if (j != 0)
      {
        i = PositionalDataSource.computeInitialLoadPosition(paramLoadInitialParams, j);
        localObject = getSQLiteQuery(i, PositionalDataSource.computeInitialLoadSize(paramLoadInitialParams, i, j));
        paramLoadInitialParams = (PositionalDataSource.LoadInitialParams)localObject;
        try
        {
          localObject = mDatabase.query((SupportSQLiteQuery)localObject);
          try
          {
            localList1 = convertRows((Cursor)localObject);
            mDatabase.setTransactionSuccessful();
          }
          catch (Throwable localThrowable1)
          {
            paramLoadInitialCallback = paramLoadInitialParams;
            paramLoadInitialParams = localThrowable1;
            break label158;
          }
          paramLoadInitialParams = null;
        }
        catch (Throwable localThrowable3)
        {
          localObject = localThrowable1;
          paramLoadInitialCallback = paramLoadInitialParams;
          paramLoadInitialParams = localThrowable3;
        }
      }
      else
      {
        i = 0;
        localThrowable2 = localThrowable3;
      }
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
      mDatabase.endTransaction();
      if (paramLoadInitialParams != null) {
        paramLoadInitialParams.release();
      }
      paramLoadInitialCallback.onResult(localThrowable2, i, j);
      return;
    }
    catch (Throwable paramLoadInitialParams)
    {
      Throwable localThrowable2;
      paramLoadInitialCallback = null;
      localObject = localThrowable2;
      label158:
      if (localObject != null) {
        ((Cursor)localObject).close();
      }
      mDatabase.endTransaction();
      if (paramLoadInitialCallback != null) {
        paramLoadInitialCallback.release();
      }
      throw paramLoadInitialParams;
    }
  }
  
  public List loadRange(int paramInt1, int paramInt2)
  {
    RoomSQLiteQuery localRoomSQLiteQuery = getSQLiteQuery(paramInt1, paramInt2);
    if (mInTransaction)
    {
      mDatabase.beginTransaction();
      try
      {
        Cursor localCursor2 = mDatabase.query(localRoomSQLiteQuery);
        localCursor1 = localCursor2;
        try
        {
          List localList2 = convertRows(localCursor2);
          mDatabase.setTransactionSuccessful();
          if (localCursor2 != null) {
            localCursor2.close();
          }
          mDatabase.endTransaction();
          localRoomSQLiteQuery.release();
          return localList2;
        }
        catch (Throwable localThrowable1) {}
        if (localCursor1 == null) {
          break label97;
        }
      }
      catch (Throwable localThrowable2)
      {
        localCursor1 = null;
      }
      localCursor1.close();
      label97:
      mDatabase.endTransaction();
      localRoomSQLiteQuery.release();
      throw localThrowable2;
    }
    Cursor localCursor1 = mDatabase.query(localRoomSQLiteQuery);
    try
    {
      List localList1 = convertRows(localCursor1);
      localCursor1.close();
      localRoomSQLiteQuery.release();
      return localList1;
    }
    catch (Throwable localThrowable3)
    {
      localCursor1.close();
      localRoomSQLiteQuery.release();
      throw localThrowable3;
    }
  }
  
  public void loadRange(PositionalDataSource.LoadRangeParams paramLoadRangeParams, PositionalDataSource.LoadRangeCallback paramLoadRangeCallback)
  {
    paramLoadRangeCallback.onResult(loadRange(startPosition, loadSize));
  }
}

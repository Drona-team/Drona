package com.facebook.react.modules.storage;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteProgram;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import com.facebook.common.logging.FLog;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.BaseJavaModule;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.GuardedAsyncTask;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.facebook.react.modules.common.ModuleDataCleaner.Cleanable;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Executor;

@ReactModule(name="AsyncSQLiteDBStorage")
public final class AsyncStorageModule
  extends ReactContextBaseJavaModule
  implements ModuleDataCleaner.Cleanable
{
  private static final int MAX_SQL_KEYS = 999;
  public static final String NAME = "AsyncSQLiteDBStorage";
  private final SerialExecutor executor;
  private ReactDatabaseSupplier mReactDatabaseSupplier;
  private boolean mShuttingDown = false;
  
  public AsyncStorageModule(ReactApplicationContext paramReactApplicationContext)
  {
    this(paramReactApplicationContext, AsyncTask.THREAD_POOL_EXECUTOR);
  }
  
  AsyncStorageModule(ReactApplicationContext paramReactApplicationContext, Executor paramExecutor)
  {
    super(paramReactApplicationContext);
    executor = new SerialExecutor(paramExecutor);
    mReactDatabaseSupplier = ReactDatabaseSupplier.getInstance(paramReactApplicationContext);
  }
  
  private boolean ensureDatabase()
  {
    return (!mShuttingDown) && (mReactDatabaseSupplier.ensureDatabase());
  }
  
  public void clear(final Callback paramCallback)
  {
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        if (!mReactDatabaseSupplier.ensureDatabase())
        {
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getDBError(null) });
          return;
        }
        paramAnonymousVarArgs = AsyncStorageModule.this;
        try
        {
          mReactDatabaseSupplier.clear();
          paramAnonymousVarArgs = paramCallback;
          paramAnonymousVarArgs.invoke(new Object[0]);
          return;
        }
        catch (Exception paramAnonymousVarArgs)
        {
          FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage()) });
        }
      }
    }.executeOnExecutor(executor, new Void[0]);
  }
  
  public void clearSensitiveData()
  {
    mReactDatabaseSupplier.clearAndCloseDatabase();
  }
  
  public void getAllKeys(final Callback paramCallback)
  {
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        if (!AsyncStorageModule.this.ensureDatabase())
        {
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getDBError(null), null });
          return;
        }
        WritableArray localWritableArray = Arguments.createArray();
        paramAnonymousVarArgs = mReactDatabaseSupplier.openDatabase().query("catalystLocalStorage", new String[] { "key" }, null, null, null, null, null);
        try
        {
          boolean bool = paramAnonymousVarArgs.moveToFirst();
          if (bool) {
            do
            {
              localWritableArray.pushString(paramAnonymousVarArgs.getString(0));
              bool = paramAnonymousVarArgs.moveToNext();
            } while (bool);
          }
          paramAnonymousVarArgs.close();
          paramCallback.invoke(new Object[] { null, localWritableArray });
          return;
        }
        catch (Throwable localThrowable) {}catch (Exception localException)
        {
          FLog.w("ReactNative", localException.getMessage(), localException);
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getError(null, localException.getMessage()), null });
          paramAnonymousVarArgs.close();
          return;
        }
        paramAnonymousVarArgs.close();
        throw localException;
      }
    }.executeOnExecutor(executor, new Void[0]);
  }
  
  public String getName()
  {
    return "AsyncSQLiteDBStorage";
  }
  
  public void initialize()
  {
    super.initialize();
    mShuttingDown = false;
  }
  
  public void multiGet(final ReadableArray paramReadableArray, final Callback paramCallback)
  {
    if (paramReadableArray == null)
    {
      paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getInvalidKeyError(null), null });
      return;
    }
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        if (!AsyncStorageModule.this.ensureDatabase())
        {
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getDBError(null), null });
          return;
        }
        paramAnonymousVarArgs = new HashSet();
        WritableArray localWritableArray = Arguments.createArray();
        int i = 0;
        Object localObject1;
        for (;;)
        {
          if (i >= paramReadableArray.size()) {
            break label438;
          }
          int k = Math.min(paramReadableArray.size() - i, 999);
          localObject1 = mReactDatabaseSupplier.openDatabase();
          Object localObject2 = AsyncLocalStorageUtil.buildKeySelection(k);
          Object localObject3 = AsyncLocalStorageUtil.buildKeySelectionArgs(paramReadableArray, i, k);
          localObject1 = ((SQLiteDatabase)localObject1).query("catalystLocalStorage", new String[] { "key", "value" }, (String)localObject2, (String[])localObject3, null, null, null);
          paramAnonymousVarArgs.clear();
          try
          {
            int j = ((Cursor)localObject1).getCount();
            localObject2 = paramReadableArray;
            int m = ((ReadableArray)localObject2).size();
            if (j != m)
            {
              j = i;
              while (j < i + k)
              {
                localObject2 = paramReadableArray;
                paramAnonymousVarArgs.add(((ReadableArray)localObject2).getString(j));
                j += 1;
              }
            }
            boolean bool = ((Cursor)localObject1).moveToFirst();
            if (bool) {
              do
              {
                localObject2 = Arguments.createArray();
                ((WritableArray)localObject2).pushString(((Cursor)localObject1).getString(0));
                ((WritableArray)localObject2).pushString(((Cursor)localObject1).getString(1));
                localWritableArray.pushArray((ReadableArray)localObject2);
                paramAnonymousVarArgs.remove(((Cursor)localObject1).getString(0));
                bool = ((Cursor)localObject1).moveToNext();
              } while (bool);
            }
            ((Cursor)localObject1).close();
            localObject1 = paramAnonymousVarArgs.iterator();
            while (((Iterator)localObject1).hasNext())
            {
              localObject2 = (String)((Iterator)localObject1).next();
              localObject3 = Arguments.createArray();
              ((WritableArray)localObject3).pushString((String)localObject2);
              ((WritableArray)localObject3).pushNull();
              localWritableArray.pushArray((ReadableArray)localObject3);
            }
            paramAnonymousVarArgs.clear();
            i += 999;
          }
          catch (Throwable paramAnonymousVarArgs) {}catch (Exception paramAnonymousVarArgs)
          {
            FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
            paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage()), null });
            ((Cursor)localObject1).close();
            return;
          }
        }
        ((Cursor)localObject1).close();
        throw paramAnonymousVarArgs;
        label438:
        paramCallback.invoke(new Object[] { null, localWritableArray });
      }
    }.executeOnExecutor(executor, new Void[0]);
  }
  
  public void multiMerge(final ReadableArray paramReadableArray, final Callback paramCallback)
  {
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        boolean bool = AsyncStorageModule.this.ensureDatabase();
        WritableMap localWritableMap2 = null;
        if (!bool)
        {
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getDBError(null) });
          return;
        }
        Object localObject2 = AsyncStorageModule.this;
        4 local4 = this;
        Object localObject1 = local4;
        paramAnonymousVarArgs = local4;
        try
        {
          mReactDatabaseSupplier.openDatabase().beginTransaction();
          int i = 0;
          for (;;)
          {
            localObject2 = paramReadableArray;
            localObject1 = local4;
            paramAnonymousVarArgs = local4;
            int j = ((ReadableArray)localObject2).size();
            if (i >= j) {
              break;
            }
            localObject2 = paramReadableArray;
            localObject1 = local4;
            paramAnonymousVarArgs = local4;
            j = ((ReadableArray)localObject2).getArray(i).size();
            if (j != 2)
            {
              localObject1 = local4;
              paramAnonymousVarArgs = local4;
              localWritableMap2 = AsyncStorageErrorUtil.getInvalidValueError(null);
              paramAnonymousVarArgs = this$0;
              try
              {
                mReactDatabaseSupplier.openDatabase().endTransaction();
                return;
              }
              catch (Exception paramAnonymousVarArgs)
              {
                FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
                if (localWritableMap2 != null) {
                  return;
                }
              }
              AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage());
              return;
            }
            localObject2 = paramReadableArray;
            localObject1 = local4;
            paramAnonymousVarArgs = local4;
            localObject2 = ((ReadableArray)localObject2).getArray(i).getString(0);
            if (localObject2 == null)
            {
              localObject1 = local4;
              paramAnonymousVarArgs = local4;
              localWritableMap2 = AsyncStorageErrorUtil.getInvalidKeyError(null);
              paramAnonymousVarArgs = this$0;
              try
              {
                mReactDatabaseSupplier.openDatabase().endTransaction();
                return;
              }
              catch (Exception paramAnonymousVarArgs)
              {
                FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
                if (localWritableMap2 != null) {
                  return;
                }
              }
              AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage());
              return;
            }
            localObject2 = paramReadableArray;
            localObject1 = local4;
            paramAnonymousVarArgs = local4;
            localObject2 = ((ReadableArray)localObject2).getArray(i).getString(1);
            if (localObject2 == null)
            {
              localObject1 = local4;
              paramAnonymousVarArgs = local4;
              localWritableMap2 = AsyncStorageErrorUtil.getInvalidValueError(null);
              paramAnonymousVarArgs = this$0;
              try
              {
                mReactDatabaseSupplier.openDatabase().endTransaction();
                return;
              }
              catch (Exception paramAnonymousVarArgs)
              {
                FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
                if (localWritableMap2 != null) {
                  return;
                }
              }
              AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage());
              return;
            }
            localObject1 = local4;
            localObject2 = this$0;
            localObject1 = local4;
            paramAnonymousVarArgs = local4;
            localObject2 = mReactDatabaseSupplier.openDatabase();
            localObject1 = local4;
            Object localObject3 = paramReadableArray;
            localObject1 = local4;
            paramAnonymousVarArgs = local4;
            localObject3 = ((ReadableArray)localObject3).getArray(i).getString(0);
            ReadableArray localReadableArray = paramReadableArray;
            localObject1 = local4;
            paramAnonymousVarArgs = local4;
            bool = AsyncLocalStorageUtil.mergeImpl((SQLiteDatabase)localObject2, (String)localObject3, localReadableArray.getArray(i).getString(1));
            if (!bool)
            {
              localObject1 = local4;
              paramAnonymousVarArgs = local4;
              localWritableMap2 = AsyncStorageErrorUtil.getDBError(null);
              paramAnonymousVarArgs = this$0;
              try
              {
                mReactDatabaseSupplier.openDatabase().endTransaction();
                return;
              }
              catch (Exception paramAnonymousVarArgs)
              {
                FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
                if (localWritableMap2 != null) {
                  return;
                }
              }
              AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage());
              return;
            }
            i += 1;
          }
          localObject2 = this$0;
          localObject1 = local4;
          paramAnonymousVarArgs = local4;
          mReactDatabaseSupplier.openDatabase().setTransactionSuccessful();
          paramAnonymousVarArgs = this$0;
          try
          {
            mReactDatabaseSupplier.openDatabase().endTransaction();
            paramAnonymousVarArgs = localWritableMap2;
            localObject1 = local4;
          }
          catch (Exception paramAnonymousVarArgs)
          {
            FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
            paramAnonymousVarArgs = AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage());
            localObject1 = local4;
          }
          try
          {
            mReactDatabaseSupplier.openDatabase().endTransaction();
            paramAnonymousVarArgs = localWritableMap1;
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              WritableMap localWritableMap1;
              FLog.w("ReactNative", localException3.getMessage(), localException3);
              paramAnonymousVarArgs = localWritableMap1;
              if (localWritableMap2 == null) {
                paramAnonymousVarArgs = AsyncStorageErrorUtil.getError(null, localException3.getMessage());
              }
            }
          }
        }
        catch (Throwable paramAnonymousVarArgs) {}catch (Exception localException2)
        {
          localObject1 = paramAnonymousVarArgs;
          FLog.w("ReactNative", ((Exception)localException2).getMessage(), localException2);
          localObject1 = paramAnonymousVarArgs;
          localWritableMap2 = AsyncStorageErrorUtil.getError(null, ((Exception)localException2).getMessage());
          localWritableMap1 = localWritableMap2;
          localObject2 = this$0;
          localObject1 = paramAnonymousVarArgs;
        }
        if (paramAnonymousVarArgs != null)
        {
          paramCallback.invoke(new Object[] { paramAnonymousVarArgs });
          return;
        }
        paramCallback.invoke(new Object[0]);
        return;
        localObject1 = this$0;
        try
        {
          mReactDatabaseSupplier.openDatabase().endTransaction();
        }
        catch (Exception localException1)
        {
          FLog.w("ReactNative", localException1.getMessage(), localException1);
          AsyncStorageErrorUtil.getError(null, localException1.getMessage());
        }
        throw paramAnonymousVarArgs;
      }
    }.executeOnExecutor(executor, new Void[0]);
  }
  
  public void multiRemove(final ReadableArray paramReadableArray, final Callback paramCallback)
  {
    if (paramReadableArray.size() == 0)
    {
      paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getInvalidKeyError(null) });
      return;
    }
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        boolean bool = AsyncStorageModule.this.ensureDatabase();
        WritableMap localWritableMap2 = null;
        if (!bool)
        {
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getDBError(null) });
          return;
        }
        Object localObject2 = AsyncStorageModule.this;
        3 local3 = this;
        Object localObject1 = local3;
        paramAnonymousVarArgs = local3;
        try
        {
          mReactDatabaseSupplier.openDatabase().beginTransaction();
          int i = 0;
          for (;;)
          {
            localObject2 = paramReadableArray;
            localObject1 = local3;
            paramAnonymousVarArgs = local3;
            int j = ((ReadableArray)localObject2).size();
            if (i >= j) {
              break;
            }
            localObject2 = paramReadableArray;
            localObject1 = local3;
            paramAnonymousVarArgs = local3;
            j = ((ReadableArray)localObject2).size();
            localObject1 = local3;
            paramAnonymousVarArgs = local3;
            j = Math.min(j - i, 999);
            localObject1 = local3;
            localObject2 = this$0;
            localObject1 = local3;
            paramAnonymousVarArgs = local3;
            localObject2 = mReactDatabaseSupplier.openDatabase();
            localObject1 = local3;
            paramAnonymousVarArgs = local3;
            String str = AsyncLocalStorageUtil.buildKeySelection(j);
            ReadableArray localReadableArray = paramReadableArray;
            localObject1 = local3;
            paramAnonymousVarArgs = local3;
            ((SQLiteDatabase)localObject2).delete("catalystLocalStorage", str, AsyncLocalStorageUtil.buildKeySelectionArgs(localReadableArray, i, j));
            i += 999;
          }
          localObject2 = this$0;
          localObject1 = local3;
          paramAnonymousVarArgs = local3;
          mReactDatabaseSupplier.openDatabase().setTransactionSuccessful();
          paramAnonymousVarArgs = this$0;
          try
          {
            mReactDatabaseSupplier.openDatabase().endTransaction();
            paramAnonymousVarArgs = localWritableMap2;
            localObject1 = local3;
          }
          catch (Exception paramAnonymousVarArgs)
          {
            FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
            paramAnonymousVarArgs = AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage());
            localObject1 = local3;
          }
          try
          {
            mReactDatabaseSupplier.openDatabase().endTransaction();
            paramAnonymousVarArgs = localWritableMap1;
          }
          catch (Exception localException3)
          {
            for (;;)
            {
              WritableMap localWritableMap1;
              FLog.w("ReactNative", localException3.getMessage(), localException3);
              paramAnonymousVarArgs = localWritableMap1;
              if (localWritableMap2 == null) {
                paramAnonymousVarArgs = AsyncStorageErrorUtil.getError(null, localException3.getMessage());
              }
            }
          }
        }
        catch (Throwable paramAnonymousVarArgs) {}catch (Exception localException2)
        {
          localObject1 = paramAnonymousVarArgs;
          FLog.w("ReactNative", localException2.getMessage(), localException2);
          localObject1 = paramAnonymousVarArgs;
          localWritableMap2 = AsyncStorageErrorUtil.getError(null, localException2.getMessage());
          localWritableMap1 = localWritableMap2;
          localObject2 = this$0;
          localObject1 = paramAnonymousVarArgs;
        }
        if (paramAnonymousVarArgs != null)
        {
          paramCallback.invoke(new Object[] { paramAnonymousVarArgs });
          return;
        }
        paramCallback.invoke(new Object[0]);
        return;
        localObject1 = this$0;
        try
        {
          mReactDatabaseSupplier.openDatabase().endTransaction();
        }
        catch (Exception localException1)
        {
          FLog.w("ReactNative", localException1.getMessage(), localException1);
          AsyncStorageErrorUtil.getError(null, localException1.getMessage());
        }
        throw paramAnonymousVarArgs;
      }
    }.executeOnExecutor(executor, new Void[0]);
  }
  
  public void multiSet(final ReadableArray paramReadableArray, final Callback paramCallback)
  {
    if (paramReadableArray.size() == 0)
    {
      paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getInvalidKeyError(null) });
      return;
    }
    new GuardedAsyncTask(getReactApplicationContext())
    {
      protected void doInBackgroundGuarded(Void... paramAnonymousVarArgs)
      {
        boolean bool = AsyncStorageModule.this.ensureDatabase();
        paramAnonymousVarArgs = null;
        if (!bool)
        {
          paramCallback.invoke(new Object[] { AsyncStorageErrorUtil.getDBError(null) });
          return;
        }
        Object localObject1 = mReactDatabaseSupplier.openDatabase().compileStatement("INSERT OR REPLACE INTO catalystLocalStorage VALUES (?, ?);");
        Object localObject3 = AsyncStorageModule.this;
        try
        {
          mReactDatabaseSupplier.openDatabase().beginTransaction();
          int i = 0;
          for (;;)
          {
            localObject3 = paramReadableArray;
            int j = ((ReadableArray)localObject3).size();
            if (i >= j) {
              break;
            }
            localObject3 = paramReadableArray;
            j = ((ReadableArray)localObject3).getArray(i).size();
            if (j != 2)
            {
              paramAnonymousVarArgs = AsyncStorageErrorUtil.getInvalidValueError(null);
              localObject1 = AsyncStorageModule.this;
              try
              {
                mReactDatabaseSupplier.openDatabase().endTransaction();
                return;
              }
              catch (Exception localException1)
              {
                FLog.w("ReactNative", localException1.getMessage(), localException1);
                if (paramAnonymousVarArgs != null) {
                  return;
                }
              }
              AsyncStorageErrorUtil.getError(null, localException1.getMessage());
              return;
            }
            localObject3 = paramReadableArray;
            localObject3 = ((ReadableArray)localObject3).getArray(i).getString(0);
            if (localObject3 == null)
            {
              paramAnonymousVarArgs = AsyncStorageErrorUtil.getInvalidKeyError(null);
              AsyncStorageModule localAsyncStorageModule1 = AsyncStorageModule.this;
              try
              {
                mReactDatabaseSupplier.openDatabase().endTransaction();
                return;
              }
              catch (Exception localException2)
              {
                FLog.w("ReactNative", localException2.getMessage(), localException2);
                if (paramAnonymousVarArgs != null) {
                  return;
                }
              }
              AsyncStorageErrorUtil.getError(null, localException2.getMessage());
              return;
            }
            localObject3 = paramReadableArray;
            localObject3 = ((ReadableArray)localObject3).getArray(i).getString(1);
            if (localObject3 == null)
            {
              paramAnonymousVarArgs = AsyncStorageErrorUtil.getInvalidValueError(null);
              AsyncStorageModule localAsyncStorageModule2 = AsyncStorageModule.this;
              try
              {
                mReactDatabaseSupplier.openDatabase().endTransaction();
                return;
              }
              catch (Exception localException3)
              {
                FLog.w("ReactNative", localException3.getMessage(), localException3);
                if (paramAnonymousVarArgs != null) {
                  return;
                }
              }
              AsyncStorageErrorUtil.getError(null, localException3.getMessage());
              return;
            }
            localException3.clearBindings();
            localObject3 = paramReadableArray;
            localException3.bindString(1, ((ReadableArray)localObject3).getArray(i).getString(0));
            localObject3 = paramReadableArray;
            localException3.bindString(2, ((ReadableArray)localObject3).getArray(i).getString(1));
            localException3.execute();
            i += 1;
          }
          localObject2 = AsyncStorageModule.this;
          mReactDatabaseSupplier.openDatabase().setTransactionSuccessful();
          localObject2 = AsyncStorageModule.this;
          try
          {
            mReactDatabaseSupplier.openDatabase().endTransaction();
          }
          catch (Exception paramAnonymousVarArgs)
          {
            FLog.w("ReactNative", paramAnonymousVarArgs.getMessage(), paramAnonymousVarArgs);
            paramAnonymousVarArgs = AsyncStorageErrorUtil.getError(null, paramAnonymousVarArgs.getMessage());
          }
          try
          {
            mReactDatabaseSupplier.openDatabase().endTransaction();
          }
          catch (Exception localException5)
          {
            for (;;)
            {
              FLog.w("ReactNative", localException5.getMessage(), localException5);
              if (localObject2 == null) {
                paramAnonymousVarArgs = AsyncStorageErrorUtil.getError(null, localException5.getMessage());
              }
            }
          }
        }
        catch (Throwable paramAnonymousVarArgs) {}catch (Exception paramAnonymousVarArgs)
        {
          FLog.w("ReactNative", ((Exception)paramAnonymousVarArgs).getMessage(), paramAnonymousVarArgs);
          localObject2 = AsyncStorageErrorUtil.getError(null, ((Exception)paramAnonymousVarArgs).getMessage());
          paramAnonymousVarArgs = (Void[])localObject2;
          localObject3 = AsyncStorageModule.this;
        }
        if (paramAnonymousVarArgs != null)
        {
          paramCallback.invoke(new Object[] { paramAnonymousVarArgs });
          return;
        }
        paramCallback.invoke(new Object[0]);
        return;
        Object localObject2 = AsyncStorageModule.this;
        try
        {
          mReactDatabaseSupplier.openDatabase().endTransaction();
        }
        catch (Exception localException4)
        {
          FLog.w("ReactNative", localException4.getMessage(), localException4);
          AsyncStorageErrorUtil.getError(null, localException4.getMessage());
        }
        throw paramAnonymousVarArgs;
      }
    }.executeOnExecutor(executor, new Void[0]);
  }
  
  public void onCatalystInstanceDestroy()
  {
    mShuttingDown = true;
  }
  
  private class SerialExecutor
    implements Executor
  {
    private final Executor executor;
    private Runnable mActive;
    private final ArrayDeque<Runnable> mTasks = new ArrayDeque();
    
    SerialExecutor(Executor paramExecutor)
    {
      executor = paramExecutor;
    }
    
    public void execute(final Runnable paramRunnable)
    {
      try
      {
        mTasks.offer(new Runnable()
        {
          public void run()
          {
            try
            {
              paramRunnable.run();
              scheduleNext();
              return;
            }
            catch (Throwable localThrowable)
            {
              scheduleNext();
              throw localThrowable;
            }
          }
        });
        if (mActive == null) {
          scheduleNext();
        }
        return;
      }
      catch (Throwable paramRunnable)
      {
        throw paramRunnable;
      }
    }
    
    void scheduleNext()
    {
      try
      {
        Runnable localRunnable = (Runnable)mTasks.poll();
        mActive = localRunnable;
        if (localRunnable != null) {
          executor.execute(mActive);
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
}

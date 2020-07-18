package com.facebook.react.modules.storage;

import android.content.Context;
import android.database.sqlite.SQLiteClosable;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;

public class ReactDatabaseSupplier
  extends SQLiteOpenHelper
{
  public static final String DATABASE_NAME = "RKStorage";
  private static final int DATABASE_VERSION = 1;
  static final String KEY_COLUMN = "key";
  private static final int SLEEP_TIME_MS = 30;
  static final String TABLE_CATALYST = "catalystLocalStorage";
  static final String VALUE_COLUMN = "value";
  static final String VERSION_TABLE_CREATE = "CREATE TABLE catalystLocalStorage (key TEXT PRIMARY KEY, value TEXT NOT NULL)";
  @Nullable
  private static ReactDatabaseSupplier sReactDatabaseSupplierInstance;
  @Nullable
  private SQLiteDatabase db;
  private Context mContext;
  private long mMaximumDatabaseSize = 6291456L;
  
  private ReactDatabaseSupplier(Context paramContext)
  {
    super(paramContext, "RKStorage", null, 1);
    mContext = paramContext;
  }
  
  private void closeDatabase()
  {
    try
    {
      if ((db != null) && (db.isOpen()))
      {
        db.close();
        db = null;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private boolean deleteDatabase()
  {
    try
    {
      closeDatabase();
      boolean bool = mContext.deleteDatabase("RKStorage");
      return bool;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public static void deleteInstance()
  {
    sReactDatabaseSupplierInstance = null;
  }
  
  public static ReactDatabaseSupplier getInstance(Context paramContext)
  {
    if (sReactDatabaseSupplierInstance == null) {
      sReactDatabaseSupplierInstance = new ReactDatabaseSupplier(paramContext.getApplicationContext());
    }
    return sReactDatabaseSupplierInstance;
  }
  
  void clear()
  {
    try
    {
      openDatabase().delete("catalystLocalStorage", null, null);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void clearAndCloseDatabase()
    throws RuntimeException
  {
    try
    {
      clear();
      closeDatabase();
      FLog.d("ReactNative", "Cleaned RKStorage");
      return;
    }
    catch (Throwable localThrowable)
    {
      break label51;
      if (deleteDatabase())
      {
        FLog.d("ReactNative", "Deleted Local Database RKStorage");
        return;
      }
      throw new RuntimeException("Clearing and deleting database RKStorage failed");
      throw localThrowable;
    }
    catch (Exception localException)
    {
      label51:
      for (;;) {}
    }
  }
  
  /* Error */
  boolean ensureDatabase()
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 52	com/facebook/react/modules/storage/ReactDatabaseSupplier:db	Landroid/database/sqlite/SQLiteDatabase;
    //   6: ifnull +19 -> 25
    //   9: aload_0
    //   10: getfield 52	com/facebook/react/modules/storage/ReactDatabaseSupplier:db	Landroid/database/sqlite/SQLiteDatabase;
    //   13: invokevirtual 58	android/database/sqlite/SQLiteDatabase:isOpen	()Z
    //   16: istore_2
    //   17: iload_2
    //   18: ifeq +7 -> 25
    //   21: aload_0
    //   22: monitorexit
    //   23: iconst_1
    //   24: ireturn
    //   25: aconst_null
    //   26: astore_3
    //   27: iconst_0
    //   28: istore_1
    //   29: iload_1
    //   30: iconst_2
    //   31: if_icmpge +48 -> 79
    //   34: iload_1
    //   35: ifle +11 -> 46
    //   38: aload_0
    //   39: invokespecial 110	com/facebook/react/modules/storage/ReactDatabaseSupplier:deleteDatabase	()Z
    //   42: pop
    //   43: goto +3 -> 46
    //   46: aload_0
    //   47: invokevirtual 126	android/database/sqlite/SQLiteOpenHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   50: astore 4
    //   52: aload_0
    //   53: aload 4
    //   55: putfield 52	com/facebook/react/modules/storage/ReactDatabaseSupplier:db	Landroid/database/sqlite/SQLiteDatabase;
    //   58: goto +21 -> 79
    //   61: ldc2_w 127
    //   64: invokestatic 134	java/lang/Thread:sleep	(J)V
    //   67: goto +51 -> 118
    //   70: invokestatic 138	java/lang/Thread:currentThread	()Ljava/lang/Thread;
    //   73: invokevirtual 141	java/lang/Thread:interrupt	()V
    //   76: goto +42 -> 118
    //   79: aload_0
    //   80: getfield 52	com/facebook/react/modules/storage/ReactDatabaseSupplier:db	Landroid/database/sqlite/SQLiteDatabase;
    //   83: ifnull +19 -> 102
    //   86: aload_0
    //   87: getfield 52	com/facebook/react/modules/storage/ReactDatabaseSupplier:db	Landroid/database/sqlite/SQLiteDatabase;
    //   90: aload_0
    //   91: getfield 43	com/facebook/react/modules/storage/ReactDatabaseSupplier:mMaximumDatabaseSize	J
    //   94: invokevirtual 145	android/database/sqlite/SQLiteDatabase:setMaximumSize	(J)J
    //   97: pop2
    //   98: aload_0
    //   99: monitorexit
    //   100: iconst_1
    //   101: ireturn
    //   102: aload_3
    //   103: athrow
    //   104: astore_3
    //   105: aload_0
    //   106: monitorexit
    //   107: aload_3
    //   108: athrow
    //   109: astore 4
    //   111: goto -41 -> 70
    //   114: astore_3
    //   115: goto -54 -> 61
    //   118: iload_1
    //   119: iconst_1
    //   120: iadd
    //   121: istore_1
    //   122: goto -93 -> 29
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	125	0	this	ReactDatabaseSupplier
    //   28	94	1	i	int
    //   16	2	2	bool	boolean
    //   26	77	3	localObject	Object
    //   104	4	3	localThrowable	Throwable
    //   114	1	3	localSQLiteException	android.database.sqlite.SQLiteException
    //   50	4	4	localSQLiteDatabase	SQLiteDatabase
    //   109	1	4	localInterruptedException	InterruptedException
    // Exception table:
    //   from	to	target	type
    //   2	17	104	java/lang/Throwable
    //   38	43	104	java/lang/Throwable
    //   46	52	104	java/lang/Throwable
    //   52	58	104	java/lang/Throwable
    //   61	67	104	java/lang/Throwable
    //   70	76	104	java/lang/Throwable
    //   79	98	104	java/lang/Throwable
    //   102	104	104	java/lang/Throwable
    //   61	67	109	java/lang/InterruptedException
    //   38	43	114	android/database/sqlite/SQLiteException
    //   46	52	114	android/database/sqlite/SQLiteException
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE catalystLocalStorage (key TEXT PRIMARY KEY, value TEXT NOT NULL)");
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    if (paramInt1 != paramInt2)
    {
      deleteDatabase();
      onCreate(paramSQLiteDatabase);
    }
  }
  
  public SQLiteDatabase openDatabase()
  {
    try
    {
      ensureDatabase();
      SQLiteDatabase localSQLiteDatabase = db;
      return localSQLiteDatabase;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void setMaximumSize(long paramLong)
  {
    try
    {
      mMaximumDatabaseSize = paramLong;
      if (db != null) {
        db.setMaximumSize(mMaximumDatabaseSize);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
}

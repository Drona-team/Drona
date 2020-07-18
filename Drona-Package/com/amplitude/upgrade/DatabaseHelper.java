package com.amplitude.upgrade;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;

class DatabaseHelper
  extends SQLiteOpenHelper
{
  private static final String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);";
  private static final String CREATE_IDENTIFYS_TABLE = "CREATE TABLE IF NOT EXISTS identifys (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);";
  private static final String CREATE_LONG_STORE_TABLE = "CREATE TABLE IF NOT EXISTS long_store (key TEXT PRIMARY KEY NOT NULL, value INTEGER);";
  private static final String CREATE_STORE_TABLE = "CREATE TABLE IF NOT EXISTS store (key TEXT PRIMARY KEY NOT NULL, value TEXT);";
  private static final String EVENT_FIELD = "event";
  protected static final String EVENT_TABLE_NAME = "events";
  protected static final String IDENTIFY_TABLE_NAME = "identifys";
  private static final String ID_FIELD = "id";
  private static final String KEY_FIELD = "key";
  private static final String KEY_URL = "com.amplitude.api.DatabaseHelper";
  protected static final String LONG_STORE_TABLE_NAME = "long_store";
  protected static final String STORE_TABLE_NAME = "store";
  private static final String VALUE_FIELD = "value";
  static final Map<String, com.amplitude.api.DatabaseHelper> instances = new HashMap();
  private static final AmplitudeLog logger = AmplitudeLog.getLogger();
  private boolean callResetListenerOnDatabaseReset = true;
  private DatabaseResetListener databaseResetListener;
  File file;
  private String instanceName;
  
  protected DatabaseHelper(Context paramContext)
  {
    this(paramContext, null);
  }
  
  protected DatabaseHelper(Context paramContext, String paramString)
  {
    super(paramContext, getDatabaseName(paramString), null, 3);
    file = paramContext.getDatabasePath(getDatabaseName(paramString));
    instanceName = Utils.normalizeInstanceName(paramString);
  }
  
  /* Error */
  private long addEventToTable(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 114	android/database/sqlite/SQLiteOpenHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   6: astore 9
    //   8: new 116	android/content/ContentValues
    //   11: dup
    //   12: invokespecial 117	android/content/ContentValues:<init>	()V
    //   15: astore 10
    //   17: aload 10
    //   19: ldc 20
    //   21: aload_2
    //   22: invokevirtual 121	android/content/ContentValues:put	(Ljava/lang/String;Ljava/lang/String;)V
    //   25: aload_0
    //   26: aload 9
    //   28: aload_1
    //   29: aload 10
    //   31: invokevirtual 125	com/amplitude/upgrade/DatabaseHelper:insertEventContentValuesIntoTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Landroid/content/ContentValues;)J
    //   34: lstore 7
    //   36: lload 7
    //   38: lstore_3
    //   39: lload_3
    //   40: lstore 5
    //   42: lload 7
    //   44: ldc2_w 126
    //   47: lcmp
    //   48: ifne +45 -> 93
    //   51: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   54: astore 9
    //   56: aload 9
    //   58: ldc 35
    //   60: ldc -127
    //   62: iconst_1
    //   63: anewarray 131	java/lang/Object
    //   66: dup
    //   67: iconst_0
    //   68: aload_1
    //   69: aastore
    //   70: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   73: invokevirtual 141	com/amplitude/upgrade/AmplitudeLog:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   76: pop
    //   77: lload_3
    //   78: lstore 5
    //   80: goto +13 -> 93
    //   83: astore 9
    //   85: goto +25 -> 110
    //   88: astore 9
    //   90: goto +76 -> 166
    //   93: aload_0
    //   94: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   97: goto +125 -> 222
    //   100: astore_1
    //   101: goto +126 -> 227
    //   104: astore 9
    //   106: ldc2_w 126
    //   109: lstore_3
    //   110: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   113: ldc 35
    //   115: ldc -110
    //   117: iconst_1
    //   118: anewarray 131	java/lang/Object
    //   121: dup
    //   122: iconst_0
    //   123: aload_1
    //   124: aastore
    //   125: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   128: aload 9
    //   130: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   133: pop
    //   134: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   137: ldc -99
    //   139: iconst_1
    //   140: anewarray 131	java/lang/Object
    //   143: dup
    //   144: iconst_0
    //   145: aload_2
    //   146: aastore
    //   147: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   150: aload 9
    //   152: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   155: pop
    //   156: aload_0
    //   157: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   160: lload_3
    //   161: lstore 5
    //   163: goto -70 -> 93
    //   166: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   169: ldc 35
    //   171: ldc -110
    //   173: iconst_1
    //   174: anewarray 131	java/lang/Object
    //   177: dup
    //   178: iconst_0
    //   179: aload_1
    //   180: aastore
    //   181: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   184: aload 9
    //   186: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   189: pop
    //   190: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   193: ldc -99
    //   195: iconst_1
    //   196: anewarray 131	java/lang/Object
    //   199: dup
    //   200: iconst_0
    //   201: aload_2
    //   202: aastore
    //   203: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   206: aload 9
    //   208: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   211: pop
    //   212: aload_0
    //   213: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   216: lload_3
    //   217: lstore 5
    //   219: goto -126 -> 93
    //   222: aload_0
    //   223: monitorexit
    //   224: lload 5
    //   226: lreturn
    //   227: aload_0
    //   228: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   231: aload_1
    //   232: athrow
    //   233: astore_1
    //   234: aload_0
    //   235: monitorexit
    //   236: aload_1
    //   237: athrow
    //   238: astore 9
    //   240: ldc2_w 126
    //   243: lstore_3
    //   244: goto -78 -> 166
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	247	0	this	DatabaseHelper
    //   0	247	1	paramString1	String
    //   0	247	2	paramString2	String
    //   38	206	3	l1	long
    //   40	185	5	l2	long
    //   34	9	7	l3	long
    //   6	51	9	localObject	Object
    //   83	1	9	localStackOverflowError1	StackOverflowError
    //   88	1	9	localSQLiteException1	SQLiteException
    //   104	103	9	localStackOverflowError2	StackOverflowError
    //   238	1	9	localSQLiteException2	SQLiteException
    //   15	15	10	localContentValues	ContentValues
    // Exception table:
    //   from	to	target	type
    //   51	56	83	java/lang/StackOverflowError
    //   56	77	83	java/lang/StackOverflowError
    //   56	77	88	android/database/sqlite/SQLiteException
    //   2	8	100	java/lang/Throwable
    //   8	36	100	java/lang/Throwable
    //   51	56	100	java/lang/Throwable
    //   56	77	100	java/lang/Throwable
    //   110	160	100	java/lang/Throwable
    //   166	216	100	java/lang/Throwable
    //   2	8	104	java/lang/StackOverflowError
    //   8	36	104	java/lang/StackOverflowError
    //   93	97	233	java/lang/Throwable
    //   227	233	233	java/lang/Throwable
    //   2	8	238	android/database/sqlite/SQLiteException
    //   8	36	238	android/database/sqlite/SQLiteException
  }
  
  private static void convertIfCursorWindowException(RuntimeException paramRuntimeException)
  {
    String str = paramRuntimeException.getMessage();
    if ((!Utils.isEmptyString(str)) && (str.startsWith("Cursor window allocation of"))) {
      throw new CursorWindowAllocationException(str);
    }
    throw paramRuntimeException;
  }
  
  private void delete()
  {
    DatabaseResetListener localDatabaseResetListener2;
    Object localObject2;
    SQLiteDatabase localSQLiteDatabase3;
    for (;;)
    {
      SQLiteDatabase localSQLiteDatabase2;
      try
      {
        close();
        Object localObject1 = file;
        ((File)localObject1).delete();
        if ((databaseResetListener == null) || (!callResetListenerOnDatabaseReset)) {
          return;
        }
        callResetListenerOnDatabaseReset = false;
        try
        {
          SQLiteDatabase localSQLiteDatabase4 = getWritableDatabase();
          SQLiteDatabase localSQLiteDatabase1 = localSQLiteDatabase4;
          DatabaseResetListener localDatabaseResetListener1 = databaseResetListener;
          localObject1 = localSQLiteDatabase1;
          try
          {
            localDatabaseResetListener1.onDatabaseReset(localSQLiteDatabase4);
            callResetListenerOnDatabaseReset = true;
            if ((localSQLiteDatabase4 == null) || (!localSQLiteDatabase4.isOpen())) {
              return;
            }
            close();
            return;
          }
          catch (Throwable localThrowable1)
          {
            continue;
          }
          catch (SQLiteException localSQLiteException1) {}
          localObject1 = localSQLiteDatabase2;
        }
        catch (Throwable localThrowable2)
        {
          localObject1 = null;
        }
        catch (SQLiteException localSQLiteException2)
        {
          localSQLiteDatabase2 = null;
        }
        logger.e("com.amplitude.api.DatabaseHelper", String.format("databaseReset callback failed during delete", new Object[0]), localSQLiteException2);
        localObject1 = localSQLiteDatabase2;
        Diagnostics.getLogger().logError(String.format("DB: Failed to run databaseReset callback in delete", new Object[0]), localSQLiteException2);
        callResetListenerOnDatabaseReset = true;
        if ((localSQLiteDatabase2 == null) || (!localSQLiteDatabase2.isOpen())) {
          return;
        }
        continue;
        callResetListenerOnDatabaseReset = true;
        if ((localObject1 != null) && (((SQLiteDatabase)localObject1).isOpen())) {
          close();
        }
        throw localSQLiteDatabase2;
      }
      catch (Throwable localThrowable7) {}catch (SecurityException localSecurityException)
      {
        logger.e("com.amplitude.api.DatabaseHelper", "delete failed", localSecurityException);
        Diagnostics.getLogger().logError("DB: Failed to delete database");
        if (databaseResetListener == null) {
          return;
        }
      }
      if (!callResetListenerOnDatabaseReset) {
        return;
      }
      callResetListenerOnDatabaseReset = false;
      try
      {
        SQLiteDatabase localSQLiteDatabase5 = getWritableDatabase();
        localSQLiteDatabase2 = localSQLiteDatabase5;
        localDatabaseResetListener2 = databaseResetListener;
        localObject2 = localSQLiteDatabase2;
        try
        {
          localDatabaseResetListener2.onDatabaseReset(localSQLiteDatabase5);
          callResetListenerOnDatabaseReset = true;
          if ((localSQLiteDatabase5 == null) || (!localSQLiteDatabase5.isOpen())) {
            return;
          }
        }
        catch (Throwable localThrowable3)
        {
          break;
        }
        catch (SQLiteException localSQLiteException3) {}
        localObject2 = localSQLiteDatabase3;
      }
      catch (Throwable localThrowable4)
      {
        localObject2 = null;
      }
      catch (SQLiteException localSQLiteException4)
      {
        localSQLiteDatabase3 = null;
      }
      logger.e("com.amplitude.api.DatabaseHelper", String.format("databaseReset callback failed during delete", new Object[0]), localSQLiteException4);
      localObject2 = localSQLiteDatabase3;
      Diagnostics.getLogger().logError(String.format("DB: Failed to run databaseReset callback in delete", new Object[0]), localSQLiteException4);
      callResetListenerOnDatabaseReset = true;
      if ((localSQLiteDatabase3 == null) || (!localSQLiteDatabase3.isOpen())) {
        return;
      }
    }
    callResetListenerOnDatabaseReset = true;
    if ((localObject2 != null) && (localObject2.isOpen())) {
      close();
    }
    throw localSQLiteDatabase3;
    if ((databaseResetListener != null) && (callResetListenerOnDatabaseReset))
    {
      callResetListenerOnDatabaseReset = false;
      Object localObject3;
      for (;;)
      {
        try
        {
          SQLiteDatabase localSQLiteDatabase6 = getWritableDatabase();
          localSQLiteDatabase3 = localSQLiteDatabase6;
          DatabaseResetListener localDatabaseResetListener3 = databaseResetListener;
          localObject2 = localSQLiteDatabase3;
          try
          {
            localDatabaseResetListener3.onDatabaseReset(localSQLiteDatabase6);
            callResetListenerOnDatabaseReset = true;
            if ((localSQLiteDatabase6 == null) || (!localSQLiteDatabase6.isOpen())) {
              break label524;
            }
            close();
          }
          catch (Throwable localThrowable5)
          {
            break;
          }
          catch (SQLiteException localSQLiteException5) {}
          localObject2 = localObject3;
        }
        catch (Throwable localThrowable6)
        {
          localObject2 = null;
        }
        catch (SQLiteException localSQLiteException6)
        {
          localObject3 = null;
        }
        logger.e("com.amplitude.api.DatabaseHelper", String.format("databaseReset callback failed during delete", new Object[0]), localSQLiteException6);
        localObject2 = localObject3;
        Diagnostics.getLogger().logError(String.format("DB: Failed to run databaseReset callback in delete", new Object[0]), localSQLiteException6);
        callResetListenerOnDatabaseReset = true;
        if ((localObject3 == null) || (!localObject3.isOpen())) {
          break label524;
        }
      }
      callResetListenerOnDatabaseReset = true;
      if ((localObject2 != null) && (localObject2.isOpen())) {
        close();
      }
      throw localObject3;
    }
    label524:
    throw localDatabaseResetListener2;
  }
  
  static DatabaseHelper getDatabaseHelper(Context paramContext)
  {
    return getDatabaseHelper(paramContext, null);
  }
  
  static DatabaseHelper getDatabaseHelper(Context paramContext, String paramString)
  {
    try
    {
      String str = Utils.normalizeInstanceName(paramString);
      DatabaseHelper localDatabaseHelper = (DatabaseHelper)instances.get(str);
      paramString = localDatabaseHelper;
      if (localDatabaseHelper == null)
      {
        paramString = new DatabaseHelper(paramContext.getApplicationContext(), str);
        instances.put(str, paramString);
      }
      return paramString;
    }
    catch (Throwable paramContext)
    {
      throw paramContext;
    }
  }
  
  private static String getDatabaseName(String paramString)
  {
    if ((!Utils.isEmptyString(paramString)) && (!paramString.equals("$default_instance")))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("com.amplitude.api_");
      localStringBuilder.append(paramString);
      return localStringBuilder.toString();
    }
    return "com.amplitude.api";
  }
  
  /* Error */
  private long getEventCountFromTable(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: lconst_0
    //   3: lstore_2
    //   4: aconst_null
    //   5: astore 9
    //   7: aconst_null
    //   8: astore 10
    //   10: aconst_null
    //   11: astore 7
    //   13: aload 7
    //   15: astore 6
    //   17: aload_0
    //   18: invokevirtual 262	android/database/sqlite/SQLiteOpenHelper:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   21: astore 8
    //   23: aload 7
    //   25: astore 6
    //   27: new 245	java/lang/StringBuilder
    //   30: dup
    //   31: invokespecial 246	java/lang/StringBuilder:<init>	()V
    //   34: astore 11
    //   36: aload 7
    //   38: astore 6
    //   40: aload 11
    //   42: ldc_w 264
    //   45: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   48: pop
    //   49: aload 7
    //   51: astore 6
    //   53: aload 11
    //   55: aload_1
    //   56: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   59: pop
    //   60: aload 7
    //   62: astore 6
    //   64: aload 8
    //   66: aload 11
    //   68: invokevirtual 255	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   71: invokevirtual 268	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   74: astore 7
    //   76: aload 7
    //   78: invokevirtual 274	android/database/sqlite/SQLiteStatement:simpleQueryForLong	()J
    //   81: lstore 4
    //   83: aload 7
    //   85: ifnull +8 -> 93
    //   88: aload 7
    //   90: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   93: aload_0
    //   94: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   97: lload 4
    //   99: lstore_2
    //   100: goto +195 -> 295
    //   103: astore_1
    //   104: aload 7
    //   106: astore 6
    //   108: goto +191 -> 299
    //   111: astore 8
    //   113: goto +18 -> 131
    //   116: astore 8
    //   118: goto +100 -> 218
    //   121: astore_1
    //   122: goto +177 -> 299
    //   125: astore 8
    //   127: aload 9
    //   129: astore 7
    //   131: aload 7
    //   133: astore 6
    //   135: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   138: ldc 35
    //   140: ldc_w 279
    //   143: iconst_1
    //   144: anewarray 131	java/lang/Object
    //   147: dup
    //   148: iconst_0
    //   149: aload_1
    //   150: aastore
    //   151: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   154: aload 8
    //   156: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   159: pop
    //   160: aload 7
    //   162: astore 6
    //   164: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   167: ldc_w 281
    //   170: iconst_1
    //   171: anewarray 131	java/lang/Object
    //   174: dup
    //   175: iconst_0
    //   176: aload_1
    //   177: aastore
    //   178: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   181: aload 8
    //   183: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   186: pop
    //   187: aload 7
    //   189: astore 6
    //   191: aload_0
    //   192: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   195: aload 7
    //   197: ifnull +8 -> 205
    //   200: aload 7
    //   202: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   205: aload_0
    //   206: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   209: goto +86 -> 295
    //   212: astore 8
    //   214: aload 10
    //   216: astore 7
    //   218: aload 7
    //   220: astore 6
    //   222: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   225: ldc 35
    //   227: ldc_w 279
    //   230: iconst_1
    //   231: anewarray 131	java/lang/Object
    //   234: dup
    //   235: iconst_0
    //   236: aload_1
    //   237: aastore
    //   238: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   241: aload 8
    //   243: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   246: pop
    //   247: aload 7
    //   249: astore 6
    //   251: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   254: ldc_w 281
    //   257: iconst_1
    //   258: anewarray 131	java/lang/Object
    //   261: dup
    //   262: iconst_0
    //   263: aload_1
    //   264: aastore
    //   265: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   268: aload 8
    //   270: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   273: pop
    //   274: aload 7
    //   276: astore 6
    //   278: aload_0
    //   279: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   282: aload 7
    //   284: ifnull -79 -> 205
    //   287: aload 7
    //   289: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   292: goto -87 -> 205
    //   295: aload_0
    //   296: monitorexit
    //   297: lload_2
    //   298: lreturn
    //   299: aload 6
    //   301: ifnull +11 -> 312
    //   304: aload 6
    //   306: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   309: goto +3 -> 312
    //   312: aload_0
    //   313: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   316: aload_1
    //   317: athrow
    //   318: aload_0
    //   319: monitorexit
    //   320: aload_1
    //   321: athrow
    //   322: astore_1
    //   323: goto -5 -> 318
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	326	0	this	DatabaseHelper
    //   0	326	1	paramString	String
    //   3	295	2	l1	long
    //   81	17	4	l2	long
    //   15	290	6	localObject1	Object
    //   11	277	7	localObject2	Object
    //   21	44	8	localSQLiteDatabase	SQLiteDatabase
    //   111	1	8	localStackOverflowError1	StackOverflowError
    //   116	1	8	localSQLiteException1	SQLiteException
    //   125	57	8	localStackOverflowError2	StackOverflowError
    //   212	57	8	localSQLiteException2	SQLiteException
    //   5	123	9	localObject3	Object
    //   8	207	10	localObject4	Object
    //   34	33	11	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   76	83	103	java/lang/Throwable
    //   76	83	111	java/lang/StackOverflowError
    //   76	83	116	android/database/sqlite/SQLiteException
    //   17	23	121	java/lang/Throwable
    //   27	36	121	java/lang/Throwable
    //   40	49	121	java/lang/Throwable
    //   53	60	121	java/lang/Throwable
    //   64	76	121	java/lang/Throwable
    //   135	160	121	java/lang/Throwable
    //   164	187	121	java/lang/Throwable
    //   191	195	121	java/lang/Throwable
    //   222	247	121	java/lang/Throwable
    //   251	274	121	java/lang/Throwable
    //   278	282	121	java/lang/Throwable
    //   17	23	125	java/lang/StackOverflowError
    //   27	36	125	java/lang/StackOverflowError
    //   40	49	125	java/lang/StackOverflowError
    //   53	60	125	java/lang/StackOverflowError
    //   64	76	125	java/lang/StackOverflowError
    //   17	23	212	android/database/sqlite/SQLiteException
    //   27	36	212	android/database/sqlite/SQLiteException
    //   40	49	212	android/database/sqlite/SQLiteException
    //   53	60	212	android/database/sqlite/SQLiteException
    //   64	76	212	android/database/sqlite/SQLiteException
    //   88	93	322	java/lang/Throwable
    //   93	97	322	java/lang/Throwable
    //   200	205	322	java/lang/Throwable
    //   205	209	322	java/lang/Throwable
    //   287	292	322	java/lang/Throwable
    //   304	309	322	java/lang/Throwable
    //   312	318	322	java/lang/Throwable
  }
  
  /* Error */
  private long getNthEventIdFromTable(String paramString, long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aconst_null
    //   3: astore 11
    //   5: aconst_null
    //   6: astore 12
    //   8: aconst_null
    //   9: astore 9
    //   11: ldc2_w 126
    //   14: lstore 4
    //   16: aload 9
    //   18: astore 8
    //   20: aload_0
    //   21: invokevirtual 262	android/database/sqlite/SQLiteOpenHelper:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   24: astore 10
    //   26: aload 9
    //   28: astore 8
    //   30: new 245	java/lang/StringBuilder
    //   33: dup
    //   34: invokespecial 246	java/lang/StringBuilder:<init>	()V
    //   37: astore 13
    //   39: aload 9
    //   41: astore 8
    //   43: aload 13
    //   45: ldc_w 287
    //   48: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   51: pop
    //   52: aload 9
    //   54: astore 8
    //   56: aload 13
    //   58: aload_1
    //   59: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: pop
    //   63: aload 9
    //   65: astore 8
    //   67: aload 13
    //   69: ldc_w 289
    //   72: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   75: pop
    //   76: aload 9
    //   78: astore 8
    //   80: aload 13
    //   82: lload_2
    //   83: lconst_1
    //   84: lsub
    //   85: invokevirtual 292	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   88: pop
    //   89: aload 9
    //   91: astore 8
    //   93: aload 10
    //   95: aload 13
    //   97: invokevirtual 255	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   100: invokevirtual 268	android/database/sqlite/SQLiteDatabase:compileStatement	(Ljava/lang/String;)Landroid/database/sqlite/SQLiteStatement;
    //   103: astore 9
    //   105: aload 9
    //   107: invokevirtual 274	android/database/sqlite/SQLiteStatement:simpleQueryForLong	()J
    //   110: lstore_2
    //   111: goto +41 -> 152
    //   114: astore_1
    //   115: aload 9
    //   117: astore 8
    //   119: goto +247 -> 366
    //   122: astore 10
    //   124: goto +61 -> 185
    //   127: astore 10
    //   129: goto +147 -> 276
    //   132: astore 8
    //   134: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   137: astore 10
    //   139: aload 10
    //   141: ldc 35
    //   143: aload 8
    //   145: invokevirtual 295	com/amplitude/upgrade/AmplitudeLog:w	(Ljava/lang/String;Ljava/lang/Throwable;)I
    //   148: pop
    //   149: lload 4
    //   151: lstore_2
    //   152: lload_2
    //   153: lstore 6
    //   155: aload 9
    //   157: ifnull +11 -> 168
    //   160: aload 9
    //   162: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   165: lload_2
    //   166: lstore 6
    //   168: aload_0
    //   169: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   172: goto +189 -> 361
    //   175: astore_1
    //   176: goto +190 -> 366
    //   179: astore 10
    //   181: aload 11
    //   183: astore 9
    //   185: aload 9
    //   187: astore 8
    //   189: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   192: ldc 35
    //   194: ldc_w 297
    //   197: iconst_1
    //   198: anewarray 131	java/lang/Object
    //   201: dup
    //   202: iconst_0
    //   203: aload_1
    //   204: aastore
    //   205: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   208: aload 10
    //   210: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   213: pop
    //   214: aload 9
    //   216: astore 8
    //   218: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   221: ldc_w 299
    //   224: iconst_1
    //   225: anewarray 131	java/lang/Object
    //   228: dup
    //   229: iconst_0
    //   230: aload_1
    //   231: aastore
    //   232: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   235: aload 10
    //   237: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   240: pop
    //   241: aload 9
    //   243: astore 8
    //   245: aload_0
    //   246: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   249: lload 4
    //   251: lstore 6
    //   253: aload 9
    //   255: ifnull -87 -> 168
    //   258: aload 9
    //   260: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   263: lload 4
    //   265: lstore 6
    //   267: goto -99 -> 168
    //   270: astore 10
    //   272: aload 12
    //   274: astore 9
    //   276: aload 9
    //   278: astore 8
    //   280: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   283: ldc 35
    //   285: ldc_w 297
    //   288: iconst_1
    //   289: anewarray 131	java/lang/Object
    //   292: dup
    //   293: iconst_0
    //   294: aload_1
    //   295: aastore
    //   296: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   299: aload 10
    //   301: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   304: pop
    //   305: aload 9
    //   307: astore 8
    //   309: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   312: ldc_w 299
    //   315: iconst_1
    //   316: anewarray 131	java/lang/Object
    //   319: dup
    //   320: iconst_0
    //   321: aload_1
    //   322: aastore
    //   323: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   326: aload 10
    //   328: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   331: pop
    //   332: aload 9
    //   334: astore 8
    //   336: aload_0
    //   337: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   340: lload 4
    //   342: lstore 6
    //   344: aload 9
    //   346: ifnull -178 -> 168
    //   349: aload 9
    //   351: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   354: lload 4
    //   356: lstore 6
    //   358: goto -190 -> 168
    //   361: aload_0
    //   362: monitorexit
    //   363: lload 6
    //   365: lreturn
    //   366: aload 8
    //   368: ifnull +11 -> 379
    //   371: aload 8
    //   373: invokevirtual 277	android/database/sqlite/SQLiteClosable:close	()V
    //   376: goto +3 -> 379
    //   379: aload_0
    //   380: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   383: aload_1
    //   384: athrow
    //   385: aload_0
    //   386: monitorexit
    //   387: aload_1
    //   388: athrow
    //   389: astore_1
    //   390: goto -5 -> 385
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	393	0	this	DatabaseHelper
    //   0	393	1	paramString	String
    //   0	393	2	paramLong	long
    //   14	341	4	l1	long
    //   153	211	6	l2	long
    //   18	100	8	localObject1	Object
    //   132	12	8	localSQLiteDoneException	android.database.sqlite.SQLiteDoneException
    //   187	185	8	localObject2	Object
    //   9	341	9	localObject3	Object
    //   24	70	10	localSQLiteDatabase	SQLiteDatabase
    //   122	1	10	localStackOverflowError1	StackOverflowError
    //   127	1	10	localSQLiteException1	SQLiteException
    //   137	3	10	localAmplitudeLog	AmplitudeLog
    //   179	57	10	localStackOverflowError2	StackOverflowError
    //   270	57	10	localSQLiteException2	SQLiteException
    //   3	179	11	localObject4	Object
    //   6	267	12	localObject5	Object
    //   37	59	13	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   105	111	114	java/lang/Throwable
    //   139	149	114	java/lang/Throwable
    //   105	111	122	java/lang/StackOverflowError
    //   139	149	122	java/lang/StackOverflowError
    //   105	111	127	android/database/sqlite/SQLiteException
    //   139	149	127	android/database/sqlite/SQLiteException
    //   105	111	132	android/database/sqlite/SQLiteDoneException
    //   20	26	175	java/lang/Throwable
    //   30	39	175	java/lang/Throwable
    //   43	52	175	java/lang/Throwable
    //   56	63	175	java/lang/Throwable
    //   67	76	175	java/lang/Throwable
    //   80	89	175	java/lang/Throwable
    //   93	105	175	java/lang/Throwable
    //   189	214	175	java/lang/Throwable
    //   218	241	175	java/lang/Throwable
    //   245	249	175	java/lang/Throwable
    //   280	305	175	java/lang/Throwable
    //   309	332	175	java/lang/Throwable
    //   336	340	175	java/lang/Throwable
    //   20	26	179	java/lang/StackOverflowError
    //   30	39	179	java/lang/StackOverflowError
    //   43	52	179	java/lang/StackOverflowError
    //   56	63	179	java/lang/StackOverflowError
    //   67	76	179	java/lang/StackOverflowError
    //   80	89	179	java/lang/StackOverflowError
    //   93	105	179	java/lang/StackOverflowError
    //   20	26	270	android/database/sqlite/SQLiteException
    //   30	39	270	android/database/sqlite/SQLiteException
    //   43	52	270	android/database/sqlite/SQLiteException
    //   56	63	270	android/database/sqlite/SQLiteException
    //   67	76	270	android/database/sqlite/SQLiteException
    //   80	89	270	android/database/sqlite/SQLiteException
    //   93	105	270	android/database/sqlite/SQLiteException
    //   160	165	389	java/lang/Throwable
    //   168	172	389	java/lang/Throwable
    //   258	263	389	java/lang/Throwable
    //   349	354	389	java/lang/Throwable
    //   371	376	389	java/lang/Throwable
    //   379	385	389	java/lang/Throwable
  }
  
  private void handleIfCursorRowTooLargeException(IllegalStateException paramIllegalStateException)
  {
    String str = paramIllegalStateException.getMessage();
    if ((!Utils.isEmptyString(str)) && (str.contains("Couldn't read")) && (str.contains("CursorWindow")))
    {
      delete();
      return;
    }
    throw paramIllegalStateException;
  }
  
  /* Error */
  private void removeEventFromTable(String paramString, long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 114	android/database/sqlite/SQLiteOpenHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   6: astore 4
    //   8: new 245	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 246	java/lang/StringBuilder:<init>	()V
    //   15: astore 5
    //   17: aload 5
    //   19: ldc_w 316
    //   22: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: pop
    //   26: aload 5
    //   28: lload_2
    //   29: invokevirtual 292	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   32: pop
    //   33: aload 4
    //   35: aload_1
    //   36: aload 5
    //   38: invokevirtual 255	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   41: aconst_null
    //   42: invokevirtual 319	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   45: pop
    //   46: aload_0
    //   47: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   50: goto +121 -> 171
    //   53: astore_1
    //   54: goto +120 -> 174
    //   57: astore 4
    //   59: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   62: ldc 35
    //   64: ldc_w 321
    //   67: iconst_1
    //   68: anewarray 131	java/lang/Object
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   78: aload 4
    //   80: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   83: pop
    //   84: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   87: ldc_w 323
    //   90: iconst_1
    //   91: anewarray 131	java/lang/Object
    //   94: dup
    //   95: iconst_0
    //   96: aload_1
    //   97: aastore
    //   98: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   101: aload 4
    //   103: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   106: pop
    //   107: aload_0
    //   108: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   111: goto -65 -> 46
    //   114: astore 4
    //   116: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   119: ldc 35
    //   121: ldc_w 321
    //   124: iconst_1
    //   125: anewarray 131	java/lang/Object
    //   128: dup
    //   129: iconst_0
    //   130: aload_1
    //   131: aastore
    //   132: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   135: aload 4
    //   137: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   140: pop
    //   141: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   144: ldc_w 323
    //   147: iconst_1
    //   148: anewarray 131	java/lang/Object
    //   151: dup
    //   152: iconst_0
    //   153: aload_1
    //   154: aastore
    //   155: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   158: aload 4
    //   160: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   163: pop
    //   164: aload_0
    //   165: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   168: goto -122 -> 46
    //   171: aload_0
    //   172: monitorexit
    //   173: return
    //   174: aload_0
    //   175: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   178: aload_1
    //   179: athrow
    //   180: astore_1
    //   181: aload_0
    //   182: monitorexit
    //   183: aload_1
    //   184: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	185	0	this	DatabaseHelper
    //   0	185	1	paramString	String
    //   0	185	2	paramLong	long
    //   6	28	4	localSQLiteDatabase	SQLiteDatabase
    //   57	45	4	localStackOverflowError	StackOverflowError
    //   114	45	4	localSQLiteException	SQLiteException
    //   15	22	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   2	8	53	java/lang/Throwable
    //   8	46	53	java/lang/Throwable
    //   59	111	53	java/lang/Throwable
    //   116	168	53	java/lang/Throwable
    //   2	8	57	java/lang/StackOverflowError
    //   8	46	57	java/lang/StackOverflowError
    //   2	8	114	android/database/sqlite/SQLiteException
    //   8	46	114	android/database/sqlite/SQLiteException
    //   46	50	180	java/lang/Throwable
    //   174	180	180	java/lang/Throwable
  }
  
  /* Error */
  private void removeEventsFromTable(String paramString, long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: invokevirtual 114	android/database/sqlite/SQLiteOpenHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   6: astore 4
    //   8: new 245	java/lang/StringBuilder
    //   11: dup
    //   12: invokespecial 246	java/lang/StringBuilder:<init>	()V
    //   15: astore 5
    //   17: aload 5
    //   19: ldc_w 326
    //   22: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   25: pop
    //   26: aload 5
    //   28: lload_2
    //   29: invokevirtual 292	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   32: pop
    //   33: aload 4
    //   35: aload_1
    //   36: aload 5
    //   38: invokevirtual 255	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   41: aconst_null
    //   42: invokevirtual 319	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   45: pop
    //   46: aload_0
    //   47: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   50: goto +121 -> 171
    //   53: astore_1
    //   54: goto +120 -> 174
    //   57: astore 4
    //   59: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   62: ldc 35
    //   64: ldc_w 328
    //   67: iconst_1
    //   68: anewarray 131	java/lang/Object
    //   71: dup
    //   72: iconst_0
    //   73: aload_1
    //   74: aastore
    //   75: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   78: aload 4
    //   80: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   83: pop
    //   84: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   87: ldc_w 330
    //   90: iconst_1
    //   91: anewarray 131	java/lang/Object
    //   94: dup
    //   95: iconst_0
    //   96: aload_1
    //   97: aastore
    //   98: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   101: aload 4
    //   103: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   106: pop
    //   107: aload_0
    //   108: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   111: goto -65 -> 46
    //   114: astore 4
    //   116: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   119: ldc 35
    //   121: ldc_w 328
    //   124: iconst_1
    //   125: anewarray 131	java/lang/Object
    //   128: dup
    //   129: iconst_0
    //   130: aload_1
    //   131: aastore
    //   132: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   135: aload 4
    //   137: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   140: pop
    //   141: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   144: ldc_w 330
    //   147: iconst_1
    //   148: anewarray 131	java/lang/Object
    //   151: dup
    //   152: iconst_0
    //   153: aload_1
    //   154: aastore
    //   155: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   158: aload 4
    //   160: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   163: pop
    //   164: aload_0
    //   165: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   168: goto -122 -> 46
    //   171: aload_0
    //   172: monitorexit
    //   173: return
    //   174: aload_0
    //   175: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   178: aload_1
    //   179: athrow
    //   180: astore_1
    //   181: aload_0
    //   182: monitorexit
    //   183: aload_1
    //   184: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	185	0	this	DatabaseHelper
    //   0	185	1	paramString	String
    //   0	185	2	paramLong	long
    //   6	28	4	localSQLiteDatabase	SQLiteDatabase
    //   57	45	4	localStackOverflowError	StackOverflowError
    //   114	45	4	localSQLiteException	SQLiteException
    //   15	22	5	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   2	8	53	java/lang/Throwable
    //   8	46	53	java/lang/Throwable
    //   59	111	53	java/lang/Throwable
    //   116	168	53	java/lang/Throwable
    //   2	8	57	java/lang/StackOverflowError
    //   8	46	57	java/lang/StackOverflowError
    //   2	8	114	android/database/sqlite/SQLiteException
    //   8	46	114	android/database/sqlite/SQLiteException
    //   46	50	180	java/lang/Throwable
    //   174	180	180	java/lang/Throwable
  }
  
  private void resetDatabase(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS store");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS long_store");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS events");
    paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS identifys");
    onCreate(paramSQLiteDatabase);
  }
  
  long addEvent(String paramString)
  {
    try
    {
      long l = addEventToTable("events", paramString);
      return l;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  long addIdentify(String paramString)
  {
    try
    {
      long l = addEventToTable("identifys", paramString);
      return l;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  boolean dbFileExists()
  {
    return file.exists();
  }
  
  /* Error */
  long deleteKeyFromTable(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc2_w 126
    //   5: lstore 4
    //   7: aload_0
    //   8: invokevirtual 114	android/database/sqlite/SQLiteOpenHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   11: astore 6
    //   13: aload 6
    //   15: aload_1
    //   16: ldc_w 356
    //   19: iconst_1
    //   20: anewarray 133	java/lang/String
    //   23: dup
    //   24: iconst_0
    //   25: aload_2
    //   26: aastore
    //   27: invokevirtual 319	android/database/sqlite/SQLiteDatabase:delete	(Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;)I
    //   30: istore_3
    //   31: iload_3
    //   32: i2l
    //   33: lstore 4
    //   35: aload_0
    //   36: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   39: goto +125 -> 164
    //   42: astore_1
    //   43: goto +126 -> 169
    //   46: astore 6
    //   48: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   51: ldc 35
    //   53: ldc_w 358
    //   56: iconst_1
    //   57: anewarray 131	java/lang/Object
    //   60: dup
    //   61: iconst_0
    //   62: aload_1
    //   63: aastore
    //   64: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   67: aload 6
    //   69: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   72: pop
    //   73: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   76: ldc_w 360
    //   79: iconst_1
    //   80: anewarray 131	java/lang/Object
    //   83: dup
    //   84: iconst_0
    //   85: aload_2
    //   86: aastore
    //   87: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   90: aload 6
    //   92: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   95: pop
    //   96: aload_0
    //   97: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   100: aload_0
    //   101: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   104: goto +60 -> 164
    //   107: astore 6
    //   109: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   112: ldc 35
    //   114: ldc_w 358
    //   117: iconst_1
    //   118: anewarray 131	java/lang/Object
    //   121: dup
    //   122: iconst_0
    //   123: aload_1
    //   124: aastore
    //   125: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   128: aload 6
    //   130: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   133: pop
    //   134: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   137: ldc_w 360
    //   140: iconst_1
    //   141: anewarray 131	java/lang/Object
    //   144: dup
    //   145: iconst_0
    //   146: aload_2
    //   147: aastore
    //   148: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   151: aload 6
    //   153: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   156: pop
    //   157: aload_0
    //   158: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   161: goto -61 -> 100
    //   164: aload_0
    //   165: monitorexit
    //   166: lload 4
    //   168: lreturn
    //   169: aload_0
    //   170: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   173: aload_1
    //   174: athrow
    //   175: astore_1
    //   176: aload_0
    //   177: monitorexit
    //   178: aload_1
    //   179: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	180	0	this	DatabaseHelper
    //   0	180	1	paramString1	String
    //   0	180	2	paramString2	String
    //   30	2	3	i	int
    //   5	162	4	l	long
    //   11	3	6	localSQLiteDatabase	SQLiteDatabase
    //   46	45	6	localStackOverflowError	StackOverflowError
    //   107	45	6	localSQLiteException	SQLiteException
    // Exception table:
    //   from	to	target	type
    //   7	13	42	java/lang/Throwable
    //   13	31	42	java/lang/Throwable
    //   48	100	42	java/lang/Throwable
    //   109	161	42	java/lang/Throwable
    //   7	13	46	java/lang/StackOverflowError
    //   13	31	46	java/lang/StackOverflowError
    //   7	13	107	android/database/sqlite/SQLiteException
    //   13	31	107	android/database/sqlite/SQLiteException
    //   35	39	175	java/lang/Throwable
    //   100	104	175	java/lang/Throwable
    //   169	175	175	java/lang/Throwable
  }
  
  long getEventCount()
  {
    try
    {
      long l = getEventCountFromTable("events");
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  List getEvents(long paramLong1, long paramLong2)
    throws JSONException
  {
    try
    {
      List localList = getEventsFromTable("events", paramLong1, paramLong2);
      return localList;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  /* Error */
  protected List getEventsFromTable(String paramString, long paramLong1, long paramLong2)
    throws JSONException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: new 376	java/util/LinkedList
    //   5: dup
    //   6: invokespecial 377	java/util/LinkedList:<init>	()V
    //   9: astore 10
    //   11: aload_0
    //   12: invokevirtual 262	android/database/sqlite/SQLiteOpenHelper:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   15: astore 9
    //   17: lload_2
    //   18: lconst_0
    //   19: lcmp
    //   20: iflt +70 -> 90
    //   23: new 245	java/lang/StringBuilder
    //   26: dup
    //   27: invokespecial 246	java/lang/StringBuilder:<init>	()V
    //   30: astore 7
    //   32: aload 7
    //   34: ldc_w 326
    //   37: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   40: pop
    //   41: aload 7
    //   43: lload_2
    //   44: invokevirtual 292	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   47: pop
    //   48: aload 7
    //   50: invokevirtual 255	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   53: astore 7
    //   55: goto +38 -> 93
    //   58: astore 9
    //   60: aconst_null
    //   61: astore 8
    //   63: goto +294 -> 357
    //   66: astore 9
    //   68: aconst_null
    //   69: astore 8
    //   71: goto +342 -> 413
    //   74: astore 9
    //   76: aconst_null
    //   77: astore 8
    //   79: goto +394 -> 473
    //   82: astore 9
    //   84: aconst_null
    //   85: astore 8
    //   87: goto +470 -> 557
    //   90: aconst_null
    //   91: astore 7
    //   93: lload 4
    //   95: lconst_0
    //   96: lcmp
    //   97: iflt +39 -> 136
    //   100: new 245	java/lang/StringBuilder
    //   103: dup
    //   104: invokespecial 246	java/lang/StringBuilder:<init>	()V
    //   107: astore 8
    //   109: aload 8
    //   111: ldc_w 379
    //   114: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   117: pop
    //   118: aload 8
    //   120: lload 4
    //   122: invokevirtual 292	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   125: pop
    //   126: aload 8
    //   128: invokevirtual 255	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   131: astore 8
    //   133: goto +6 -> 139
    //   136: aconst_null
    //   137: astore 8
    //   139: aload_0
    //   140: aload 9
    //   142: aload_1
    //   143: iconst_2
    //   144: anewarray 133	java/lang/String
    //   147: dup
    //   148: iconst_0
    //   149: ldc 29
    //   151: aastore
    //   152: dup
    //   153: iconst_1
    //   154: ldc 20
    //   156: aastore
    //   157: aload 7
    //   159: aconst_null
    //   160: aconst_null
    //   161: aconst_null
    //   162: ldc_w 381
    //   165: aload 8
    //   167: invokevirtual 385	com/amplitude/upgrade/DatabaseHelper:queryDb	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   170: astore 9
    //   172: aload 9
    //   174: astore 8
    //   176: aload 8
    //   178: astore 7
    //   180: aload 9
    //   182: invokeinterface 390 1 0
    //   187: istore 6
    //   189: iload 6
    //   191: ifeq +95 -> 286
    //   194: aload 8
    //   196: astore 7
    //   198: aload 9
    //   200: iconst_0
    //   201: invokeinterface 394 2 0
    //   206: lstore_2
    //   207: aload 8
    //   209: astore 7
    //   211: aload 9
    //   213: iconst_1
    //   214: invokeinterface 398 2 0
    //   219: astore 11
    //   221: aload 8
    //   223: astore 7
    //   225: aload 11
    //   227: invokestatic 176	com/amplitude/upgrade/Utils:isEmptyString	(Ljava/lang/String;)Z
    //   230: istore 6
    //   232: iload 6
    //   234: ifeq +6 -> 240
    //   237: goto -61 -> 176
    //   240: aload 8
    //   242: astore 7
    //   244: new 400	org/json/JSONObject
    //   247: dup
    //   248: aload 11
    //   250: invokespecial 401	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   253: astore 11
    //   255: aload 8
    //   257: astore 7
    //   259: aload 11
    //   261: ldc_w 403
    //   264: lload_2
    //   265: invokevirtual 406	org/json/JSONObject:put	(Ljava/lang/String;J)Lorg/json/JSONObject;
    //   268: pop
    //   269: aload 8
    //   271: astore 7
    //   273: aload 10
    //   275: aload 11
    //   277: invokeinterface 411 2 0
    //   282: pop
    //   283: goto -107 -> 176
    //   286: aload 9
    //   288: ifnull +10 -> 298
    //   291: aload 9
    //   293: invokeinterface 412 1 0
    //   298: aload_0
    //   299: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   302: goto +334 -> 636
    //   305: astore 9
    //   307: goto +50 -> 357
    //   310: astore 9
    //   312: goto +101 -> 413
    //   315: astore 9
    //   317: goto +156 -> 473
    //   320: astore 9
    //   322: goto +235 -> 557
    //   325: astore 9
    //   327: goto +27 -> 354
    //   330: astore 9
    //   332: goto +78 -> 410
    //   335: astore 9
    //   337: goto +133 -> 470
    //   340: astore 9
    //   342: goto +212 -> 554
    //   345: astore_1
    //   346: aconst_null
    //   347: astore 7
    //   349: goto +293 -> 642
    //   352: astore 9
    //   354: aconst_null
    //   355: astore 8
    //   357: aload 8
    //   359: astore 7
    //   361: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   364: ldc_w 414
    //   367: iconst_1
    //   368: anewarray 131	java/lang/Object
    //   371: dup
    //   372: iconst_0
    //   373: aload_1
    //   374: aastore
    //   375: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   378: aload 9
    //   380: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   383: pop
    //   384: aload 8
    //   386: astore 7
    //   388: aload 9
    //   390: invokestatic 416	com/amplitude/upgrade/DatabaseHelper:convertIfCursorWindowException	(Ljava/lang/RuntimeException;)V
    //   393: aload 8
    //   395: ifnull -97 -> 298
    //   398: aload 8
    //   400: invokeinterface 412 1 0
    //   405: goto -107 -> 298
    //   408: astore 9
    //   410: aconst_null
    //   411: astore 8
    //   413: aload 8
    //   415: astore 7
    //   417: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   420: ldc_w 414
    //   423: iconst_1
    //   424: anewarray 131	java/lang/Object
    //   427: dup
    //   428: iconst_0
    //   429: aload_1
    //   430: aastore
    //   431: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   434: aload 9
    //   436: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   439: pop
    //   440: aload 8
    //   442: astore 7
    //   444: aload_0
    //   445: aload 9
    //   447: checkcast 303	java/lang/IllegalStateException
    //   450: invokespecial 418	com/amplitude/upgrade/DatabaseHelper:handleIfCursorRowTooLargeException	(Ljava/lang/IllegalStateException;)V
    //   453: aload 8
    //   455: ifnull -157 -> 298
    //   458: aload 8
    //   460: invokeinterface 412 1 0
    //   465: goto -167 -> 298
    //   468: astore 9
    //   470: aconst_null
    //   471: astore 8
    //   473: aload 8
    //   475: astore 7
    //   477: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   480: ldc 35
    //   482: ldc_w 420
    //   485: iconst_1
    //   486: anewarray 131	java/lang/Object
    //   489: dup
    //   490: iconst_0
    //   491: aload_1
    //   492: aastore
    //   493: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   496: aload 9
    //   498: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   501: pop
    //   502: aload 8
    //   504: astore 7
    //   506: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   509: ldc_w 414
    //   512: iconst_1
    //   513: anewarray 131	java/lang/Object
    //   516: dup
    //   517: iconst_0
    //   518: aload_1
    //   519: aastore
    //   520: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   523: aload 9
    //   525: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   528: pop
    //   529: aload 8
    //   531: astore 7
    //   533: aload_0
    //   534: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   537: aload 8
    //   539: ifnull -241 -> 298
    //   542: aload 8
    //   544: invokeinterface 412 1 0
    //   549: goto -251 -> 298
    //   552: astore 9
    //   554: aconst_null
    //   555: astore 8
    //   557: aload 8
    //   559: astore 7
    //   561: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   564: ldc 35
    //   566: ldc_w 420
    //   569: iconst_1
    //   570: anewarray 131	java/lang/Object
    //   573: dup
    //   574: iconst_0
    //   575: aload_1
    //   576: aastore
    //   577: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   580: aload 9
    //   582: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   585: pop
    //   586: aload 8
    //   588: astore 7
    //   590: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   593: ldc_w 414
    //   596: iconst_1
    //   597: anewarray 131	java/lang/Object
    //   600: dup
    //   601: iconst_0
    //   602: aload_1
    //   603: aastore
    //   604: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   607: aload 9
    //   609: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   612: pop
    //   613: aload 8
    //   615: astore 7
    //   617: aload_0
    //   618: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   621: aload 8
    //   623: ifnull -325 -> 298
    //   626: aload 8
    //   628: invokeinterface 412 1 0
    //   633: goto -335 -> 298
    //   636: aload_0
    //   637: monitorexit
    //   638: aload 10
    //   640: areturn
    //   641: astore_1
    //   642: aload 7
    //   644: ifnull +10 -> 654
    //   647: aload 7
    //   649: invokeinterface 412 1 0
    //   654: aload_0
    //   655: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   658: aload_1
    //   659: athrow
    //   660: astore_1
    //   661: aload_0
    //   662: monitorexit
    //   663: aload_1
    //   664: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	665	0	this	DatabaseHelper
    //   0	665	1	paramString	String
    //   0	665	2	paramLong1	long
    //   0	665	4	paramLong2	long
    //   187	46	6	bool	boolean
    //   30	618	7	localObject1	Object
    //   61	566	8	localObject2	Object
    //   15	1	9	localSQLiteDatabase	SQLiteDatabase
    //   58	1	9	localRuntimeException1	RuntimeException
    //   66	1	9	localIllegalStateException1	IllegalStateException
    //   74	1	9	localStackOverflowError1	StackOverflowError
    //   82	59	9	localSQLiteException1	SQLiteException
    //   170	122	9	localCursor	Cursor
    //   305	1	9	localRuntimeException2	RuntimeException
    //   310	1	9	localIllegalStateException2	IllegalStateException
    //   315	1	9	localStackOverflowError2	StackOverflowError
    //   320	1	9	localSQLiteException2	SQLiteException
    //   325	1	9	localRuntimeException3	RuntimeException
    //   330	1	9	localIllegalStateException3	IllegalStateException
    //   335	1	9	localStackOverflowError3	StackOverflowError
    //   340	1	9	localSQLiteException3	SQLiteException
    //   352	37	9	localRuntimeException4	RuntimeException
    //   408	38	9	localIllegalStateException4	IllegalStateException
    //   468	56	9	localStackOverflowError4	StackOverflowError
    //   552	56	9	localSQLiteException4	SQLiteException
    //   9	630	10	localLinkedList	java.util.LinkedList
    //   219	57	11	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   23	55	58	java/lang/RuntimeException
    //   100	133	58	java/lang/RuntimeException
    //   23	55	66	java/lang/IllegalStateException
    //   100	133	66	java/lang/IllegalStateException
    //   23	55	74	java/lang/StackOverflowError
    //   100	133	74	java/lang/StackOverflowError
    //   23	55	82	android/database/sqlite/SQLiteException
    //   100	133	82	android/database/sqlite/SQLiteException
    //   180	189	305	java/lang/RuntimeException
    //   198	207	305	java/lang/RuntimeException
    //   211	221	305	java/lang/RuntimeException
    //   225	232	305	java/lang/RuntimeException
    //   244	255	305	java/lang/RuntimeException
    //   259	269	305	java/lang/RuntimeException
    //   273	283	305	java/lang/RuntimeException
    //   180	189	310	java/lang/IllegalStateException
    //   198	207	310	java/lang/IllegalStateException
    //   211	221	310	java/lang/IllegalStateException
    //   225	232	310	java/lang/IllegalStateException
    //   244	255	310	java/lang/IllegalStateException
    //   259	269	310	java/lang/IllegalStateException
    //   273	283	310	java/lang/IllegalStateException
    //   180	189	315	java/lang/StackOverflowError
    //   198	207	315	java/lang/StackOverflowError
    //   211	221	315	java/lang/StackOverflowError
    //   225	232	315	java/lang/StackOverflowError
    //   244	255	315	java/lang/StackOverflowError
    //   259	269	315	java/lang/StackOverflowError
    //   273	283	315	java/lang/StackOverflowError
    //   180	189	320	android/database/sqlite/SQLiteException
    //   198	207	320	android/database/sqlite/SQLiteException
    //   211	221	320	android/database/sqlite/SQLiteException
    //   225	232	320	android/database/sqlite/SQLiteException
    //   244	255	320	android/database/sqlite/SQLiteException
    //   259	269	320	android/database/sqlite/SQLiteException
    //   273	283	320	android/database/sqlite/SQLiteException
    //   139	172	325	java/lang/RuntimeException
    //   139	172	330	java/lang/IllegalStateException
    //   139	172	335	java/lang/StackOverflowError
    //   139	172	340	android/database/sqlite/SQLiteException
    //   11	17	345	java/lang/Throwable
    //   23	55	345	java/lang/Throwable
    //   100	133	345	java/lang/Throwable
    //   139	172	345	java/lang/Throwable
    //   11	17	352	java/lang/RuntimeException
    //   11	17	408	java/lang/IllegalStateException
    //   11	17	468	java/lang/StackOverflowError
    //   11	17	552	android/database/sqlite/SQLiteException
    //   180	189	641	java/lang/Throwable
    //   198	207	641	java/lang/Throwable
    //   211	221	641	java/lang/Throwable
    //   225	232	641	java/lang/Throwable
    //   244	255	641	java/lang/Throwable
    //   259	269	641	java/lang/Throwable
    //   273	283	641	java/lang/Throwable
    //   361	384	641	java/lang/Throwable
    //   388	393	641	java/lang/Throwable
    //   417	440	641	java/lang/Throwable
    //   444	453	641	java/lang/Throwable
    //   477	502	641	java/lang/Throwable
    //   506	529	641	java/lang/Throwable
    //   533	537	641	java/lang/Throwable
    //   561	586	641	java/lang/Throwable
    //   590	613	641	java/lang/Throwable
    //   617	621	641	java/lang/Throwable
    //   2	11	660	java/lang/Throwable
    //   291	298	660	java/lang/Throwable
    //   298	302	660	java/lang/Throwable
    //   398	405	660	java/lang/Throwable
    //   458	465	660	java/lang/Throwable
    //   542	549	660	java/lang/Throwable
    //   626	633	660	java/lang/Throwable
    //   647	654	660	java/lang/Throwable
    //   654	660	660	java/lang/Throwable
  }
  
  long getIdentifyCount()
  {
    try
    {
      long l = getEventCountFromTable("identifys");
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  List getIdentifys(long paramLong1, long paramLong2)
    throws JSONException
  {
    try
    {
      List localList = getEventsFromTable("identifys", paramLong1, paramLong2);
      return localList;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  Long getLongValue(String paramString)
  {
    try
    {
      paramString = (Long)getValueFromTable("long_store", paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  long getNthEventId(long paramLong)
  {
    try
    {
      paramLong = getNthEventIdFromTable("events", paramLong);
      return paramLong;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  long getNthIdentifyId(long paramLong)
  {
    try
    {
      paramLong = getNthEventIdFromTable("identifys", paramLong);
      return paramLong;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  long getTotalEventCount()
  {
    try
    {
      long l1 = getEventCount();
      long l2 = getIdentifyCount();
      return l1 + l2;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  String getValue(String paramString)
  {
    try
    {
      paramString = (String)getValueFromTable("store", paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  /* Error */
  protected Object getValueFromTable(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aconst_null
    //   3: astore 9
    //   5: aconst_null
    //   6: astore 10
    //   8: aload_0
    //   9: invokevirtual 262	android/database/sqlite/SQLiteOpenHelper:getReadableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   12: astore 6
    //   14: aload_0
    //   15: aload 6
    //   17: aload_1
    //   18: iconst_2
    //   19: anewarray 133	java/lang/String
    //   22: dup
    //   23: iconst_0
    //   24: ldc 32
    //   26: aastore
    //   27: dup
    //   28: iconst_1
    //   29: ldc 44
    //   31: aastore
    //   32: ldc_w 443
    //   35: iconst_1
    //   36: anewarray 133	java/lang/String
    //   39: dup
    //   40: iconst_0
    //   41: aload_2
    //   42: aastore
    //   43: aconst_null
    //   44: aconst_null
    //   45: aconst_null
    //   46: aconst_null
    //   47: invokevirtual 385	com/amplitude/upgrade/DatabaseHelper:queryDb	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   50: astore 8
    //   52: aload 8
    //   54: astore 7
    //   56: aload 7
    //   58: astore 6
    //   60: aload 8
    //   62: invokeinterface 446 1 0
    //   67: istore_3
    //   68: aload 10
    //   70: astore 6
    //   72: iload_3
    //   73: ifeq +61 -> 134
    //   76: aload 7
    //   78: astore 6
    //   80: aload_1
    //   81: ldc 41
    //   83: invokevirtual 243	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   86: istore_3
    //   87: iload_3
    //   88: ifeq +23 -> 111
    //   91: aload 7
    //   93: astore 6
    //   95: aload 8
    //   97: iconst_1
    //   98: invokeinterface 398 2 0
    //   103: astore 10
    //   105: aload 10
    //   107: astore_1
    //   108: goto +405 -> 513
    //   111: aload 7
    //   113: astore 6
    //   115: aload 8
    //   117: iconst_1
    //   118: invokeinterface 394 2 0
    //   123: lstore 4
    //   125: lload 4
    //   127: invokestatic 450	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   130: astore_1
    //   131: goto +382 -> 513
    //   134: aload 6
    //   136: astore_1
    //   137: aload 8
    //   139: ifnull +13 -> 152
    //   142: aload 8
    //   144: invokeinterface 412 1 0
    //   149: aload 6
    //   151: astore_1
    //   152: aload_0
    //   153: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   156: goto +327 -> 483
    //   159: astore_1
    //   160: goto +28 -> 188
    //   163: astore_1
    //   164: goto +83 -> 247
    //   167: astore 8
    //   169: goto +139 -> 308
    //   172: astore 8
    //   174: goto +224 -> 398
    //   177: astore_1
    //   178: aconst_null
    //   179: astore 6
    //   181: goto +307 -> 488
    //   184: astore_1
    //   185: aconst_null
    //   186: astore 7
    //   188: aload 7
    //   190: astore 6
    //   192: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   195: ldc_w 452
    //   198: iconst_1
    //   199: anewarray 131	java/lang/Object
    //   202: dup
    //   203: iconst_0
    //   204: aload_2
    //   205: aastore
    //   206: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   209: aload_1
    //   210: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   213: pop
    //   214: aload 7
    //   216: astore 6
    //   218: aload_1
    //   219: invokestatic 416	com/amplitude/upgrade/DatabaseHelper:convertIfCursorWindowException	(Ljava/lang/RuntimeException;)V
    //   222: aload 9
    //   224: astore_1
    //   225: aload 7
    //   227: ifnull -75 -> 152
    //   230: aload 7
    //   232: invokeinterface 412 1 0
    //   237: aload 9
    //   239: astore_1
    //   240: goto -88 -> 152
    //   243: astore_1
    //   244: aconst_null
    //   245: astore 7
    //   247: aload 7
    //   249: astore 6
    //   251: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   254: ldc_w 452
    //   257: iconst_1
    //   258: anewarray 131	java/lang/Object
    //   261: dup
    //   262: iconst_0
    //   263: aload_2
    //   264: aastore
    //   265: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   268: aload_1
    //   269: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   272: pop
    //   273: aload 7
    //   275: astore 6
    //   277: aload_0
    //   278: aload_1
    //   279: invokespecial 418	com/amplitude/upgrade/DatabaseHelper:handleIfCursorRowTooLargeException	(Ljava/lang/IllegalStateException;)V
    //   282: aload 9
    //   284: astore_1
    //   285: aload 7
    //   287: ifnull -135 -> 152
    //   290: aload 7
    //   292: invokeinterface 412 1 0
    //   297: aload 9
    //   299: astore_1
    //   300: goto -148 -> 152
    //   303: astore 8
    //   305: aconst_null
    //   306: astore 7
    //   308: aload 7
    //   310: astore 6
    //   312: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   315: ldc 35
    //   317: ldc_w 454
    //   320: iconst_1
    //   321: anewarray 131	java/lang/Object
    //   324: dup
    //   325: iconst_0
    //   326: aload_1
    //   327: aastore
    //   328: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   331: aload 8
    //   333: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   336: pop
    //   337: aload 7
    //   339: astore 6
    //   341: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   344: ldc_w 452
    //   347: iconst_1
    //   348: anewarray 131	java/lang/Object
    //   351: dup
    //   352: iconst_0
    //   353: aload_2
    //   354: aastore
    //   355: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   358: aload 8
    //   360: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   363: pop
    //   364: aload 7
    //   366: astore 6
    //   368: aload_0
    //   369: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   372: aload 9
    //   374: astore_1
    //   375: aload 7
    //   377: ifnull -225 -> 152
    //   380: aload 7
    //   382: invokeinterface 412 1 0
    //   387: aload 9
    //   389: astore_1
    //   390: goto -238 -> 152
    //   393: astore 8
    //   395: aconst_null
    //   396: astore 7
    //   398: aload 7
    //   400: astore 6
    //   402: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   405: ldc 35
    //   407: ldc_w 454
    //   410: iconst_1
    //   411: anewarray 131	java/lang/Object
    //   414: dup
    //   415: iconst_0
    //   416: aload_1
    //   417: aastore
    //   418: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   421: aload 8
    //   423: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   426: pop
    //   427: aload 7
    //   429: astore 6
    //   431: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   434: ldc_w 452
    //   437: iconst_1
    //   438: anewarray 131	java/lang/Object
    //   441: dup
    //   442: iconst_0
    //   443: aload_2
    //   444: aastore
    //   445: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   448: aload 8
    //   450: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   453: pop
    //   454: aload 7
    //   456: astore 6
    //   458: aload_0
    //   459: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   462: aload 9
    //   464: astore_1
    //   465: aload 7
    //   467: ifnull -315 -> 152
    //   470: aload 7
    //   472: invokeinterface 412 1 0
    //   477: aload 9
    //   479: astore_1
    //   480: goto -328 -> 152
    //   483: aload_0
    //   484: monitorexit
    //   485: aload_1
    //   486: areturn
    //   487: astore_1
    //   488: aload 6
    //   490: ifnull +13 -> 503
    //   493: aload 6
    //   495: invokeinterface 412 1 0
    //   500: goto +3 -> 503
    //   503: aload_0
    //   504: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   507: aload_1
    //   508: athrow
    //   509: aload_0
    //   510: monitorexit
    //   511: aload_1
    //   512: athrow
    //   513: aload_1
    //   514: astore 6
    //   516: goto -382 -> 134
    //   519: astore_1
    //   520: goto -11 -> 509
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	523	0	this	DatabaseHelper
    //   0	523	1	paramString1	String
    //   0	523	2	paramString2	String
    //   67	21	3	bool	boolean
    //   123	3	4	l	long
    //   12	503	6	localObject1	Object
    //   54	417	7	localCursor1	Cursor
    //   50	93	8	localCursor2	Cursor
    //   167	1	8	localStackOverflowError1	StackOverflowError
    //   172	1	8	localSQLiteException1	SQLiteException
    //   303	56	8	localStackOverflowError2	StackOverflowError
    //   393	56	8	localSQLiteException2	SQLiteException
    //   3	475	9	localObject2	Object
    //   6	100	10	str	String
    // Exception table:
    //   from	to	target	type
    //   60	68	159	java/lang/RuntimeException
    //   80	87	159	java/lang/RuntimeException
    //   95	105	159	java/lang/RuntimeException
    //   115	125	159	java/lang/RuntimeException
    //   60	68	163	java/lang/IllegalStateException
    //   80	87	163	java/lang/IllegalStateException
    //   95	105	163	java/lang/IllegalStateException
    //   115	125	163	java/lang/IllegalStateException
    //   60	68	167	java/lang/StackOverflowError
    //   80	87	167	java/lang/StackOverflowError
    //   95	105	167	java/lang/StackOverflowError
    //   115	125	167	java/lang/StackOverflowError
    //   60	68	172	android/database/sqlite/SQLiteException
    //   80	87	172	android/database/sqlite/SQLiteException
    //   95	105	172	android/database/sqlite/SQLiteException
    //   115	125	172	android/database/sqlite/SQLiteException
    //   8	14	177	java/lang/Throwable
    //   14	52	177	java/lang/Throwable
    //   8	14	184	java/lang/RuntimeException
    //   14	52	184	java/lang/RuntimeException
    //   8	14	243	java/lang/IllegalStateException
    //   14	52	243	java/lang/IllegalStateException
    //   8	14	303	java/lang/StackOverflowError
    //   14	52	303	java/lang/StackOverflowError
    //   8	14	393	android/database/sqlite/SQLiteException
    //   14	52	393	android/database/sqlite/SQLiteException
    //   60	68	487	java/lang/Throwable
    //   80	87	487	java/lang/Throwable
    //   95	105	487	java/lang/Throwable
    //   115	125	487	java/lang/Throwable
    //   192	214	487	java/lang/Throwable
    //   218	222	487	java/lang/Throwable
    //   251	273	487	java/lang/Throwable
    //   277	282	487	java/lang/Throwable
    //   312	337	487	java/lang/Throwable
    //   341	364	487	java/lang/Throwable
    //   368	372	487	java/lang/Throwable
    //   402	427	487	java/lang/Throwable
    //   431	454	487	java/lang/Throwable
    //   458	462	487	java/lang/Throwable
    //   142	149	519	java/lang/Throwable
    //   152	156	519	java/lang/Throwable
    //   230	237	519	java/lang/Throwable
    //   290	297	519	java/lang/Throwable
    //   380	387	519	java/lang/Throwable
    //   470	477	519	java/lang/Throwable
    //   493	500	519	java/lang/Throwable
    //   503	509	519	java/lang/Throwable
  }
  
  long insertEventContentValuesIntoTable(SQLiteDatabase paramSQLiteDatabase, String paramString, ContentValues paramContentValues)
    throws SQLiteException, StackOverflowError
  {
    try
    {
      long l = paramSQLiteDatabase.insert(paramString, null, paramContentValues);
      return l;
    }
    catch (Throwable paramSQLiteDatabase)
    {
      throw paramSQLiteDatabase;
    }
  }
  
  long insertKeyValueContentValuesIntoTable(SQLiteDatabase paramSQLiteDatabase, String paramString, ContentValues paramContentValues)
    throws SQLiteException, StackOverflowError
  {
    try
    {
      long l = paramSQLiteDatabase.insertWithOnConflict(paramString, null, paramContentValues, 5);
      return l;
    }
    catch (Throwable paramSQLiteDatabase)
    {
      throw paramSQLiteDatabase;
    }
  }
  
  long insertOrReplaceKeyLongValue(String paramString, Long paramLong)
  {
    if (paramLong == null) {}
    try
    {
      long l = deleteKeyFromTable("long_store", paramString);
      break label26;
      l = insertOrReplaceKeyValueToTable("long_store", paramString, paramLong);
      label26:
      return l;
    }
    catch (Throwable paramString)
    {
      for (;;) {}
    }
    throw paramString;
  }
  
  long insertOrReplaceKeyValue(String paramString1, String paramString2)
  {
    if (paramString2 == null) {}
    try
    {
      long l = deleteKeyFromTable("store", paramString1);
      break label26;
      l = insertOrReplaceKeyValueToTable("store", paramString1, paramString2);
      label26:
      return l;
    }
    catch (Throwable paramString1)
    {
      for (;;) {}
    }
    throw paramString1;
  }
  
  long insertOrReplaceKeyValueToTable(SQLiteDatabase paramSQLiteDatabase, String paramString1, String paramString2, Object paramObject)
    throws SQLiteException, StackOverflowError
  {
    try
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("key", paramString2);
      if ((paramObject instanceof Long)) {
        localContentValues.put("value", (Long)paramObject);
      } else {
        localContentValues.put("value", (String)paramObject);
      }
      long l = insertKeyValueContentValuesIntoTable(paramSQLiteDatabase, paramString1, localContentValues);
      if (l == -1L) {
        logger.w("com.amplitude.api.DatabaseHelper", "Insert failed");
      }
      return l;
    }
    catch (Throwable paramSQLiteDatabase)
    {
      throw paramSQLiteDatabase;
    }
  }
  
  /* Error */
  long insertOrReplaceKeyValueToTable(String paramString1, String paramString2, Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: ldc2_w 126
    //   5: lstore 6
    //   7: aconst_null
    //   8: astore 11
    //   10: aconst_null
    //   11: astore 12
    //   13: aconst_null
    //   14: astore 9
    //   16: aload_0
    //   17: invokevirtual 114	android/database/sqlite/SQLiteOpenHelper:getWritableDatabase	()Landroid/database/sqlite/SQLiteDatabase;
    //   20: astore 10
    //   22: aload_0
    //   23: aload 10
    //   25: aload_1
    //   26: aload_2
    //   27: aload_3
    //   28: invokevirtual 482	com/amplitude/upgrade/DatabaseHelper:insertOrReplaceKeyValueToTable	(Landroid/database/sqlite/SQLiteDatabase;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)J
    //   31: lstore 4
    //   33: aload 10
    //   35: ifnull +15 -> 50
    //   38: aload 10
    //   40: invokevirtual 206	android/database/sqlite/SQLiteDatabase:isOpen	()Z
    //   43: ifeq +7 -> 50
    //   46: aload_0
    //   47: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   50: goto +217 -> 267
    //   53: astore_1
    //   54: aload 10
    //   56: astore 9
    //   58: goto +214 -> 272
    //   61: astore_3
    //   62: goto +16 -> 78
    //   65: astore_3
    //   66: goto +111 -> 177
    //   69: astore_1
    //   70: goto +202 -> 272
    //   73: astore_3
    //   74: aload 11
    //   76: astore 10
    //   78: aload 10
    //   80: astore 9
    //   82: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   85: ldc 35
    //   87: ldc_w 484
    //   90: iconst_1
    //   91: anewarray 131	java/lang/Object
    //   94: dup
    //   95: iconst_0
    //   96: aload_1
    //   97: aastore
    //   98: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   101: aload_3
    //   102: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   105: pop
    //   106: aload 10
    //   108: astore 9
    //   110: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   113: ldc_w 486
    //   116: iconst_1
    //   117: anewarray 131	java/lang/Object
    //   120: dup
    //   121: iconst_0
    //   122: aload_2
    //   123: aastore
    //   124: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   127: aload_3
    //   128: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   131: pop
    //   132: aload 10
    //   134: astore 9
    //   136: aload_0
    //   137: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   140: lload 6
    //   142: lstore 4
    //   144: aload 10
    //   146: ifnull +121 -> 267
    //   149: lload 6
    //   151: lstore 4
    //   153: aload 10
    //   155: invokevirtual 206	android/database/sqlite/SQLiteDatabase:isOpen	()Z
    //   158: ifeq +109 -> 267
    //   161: aload_0
    //   162: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   165: lload 6
    //   167: lstore 4
    //   169: goto +98 -> 267
    //   172: astore_3
    //   173: aload 12
    //   175: astore 10
    //   177: aload 10
    //   179: astore 9
    //   181: getstatic 73	com/amplitude/upgrade/DatabaseHelper:logger	Lcom/amplitude/upgrade/AmplitudeLog;
    //   184: ldc 35
    //   186: ldc_w 484
    //   189: iconst_1
    //   190: anewarray 131	java/lang/Object
    //   193: dup
    //   194: iconst_0
    //   195: aload_1
    //   196: aastore
    //   197: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   200: aload_3
    //   201: invokevirtual 150	com/amplitude/upgrade/AmplitudeLog:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
    //   204: pop
    //   205: aload 10
    //   207: astore 9
    //   209: invokestatic 155	com/amplitude/upgrade/Diagnostics:getLogger	()Lcom/amplitude/upgrade/Diagnostics;
    //   212: ldc_w 486
    //   215: iconst_1
    //   216: anewarray 131	java/lang/Object
    //   219: dup
    //   220: iconst_0
    //   221: aload_2
    //   222: aastore
    //   223: invokestatic 137	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   226: aload_3
    //   227: invokevirtual 161	com/amplitude/upgrade/Diagnostics:logError	(Ljava/lang/String;Ljava/lang/Throwable;)Lcom/amplitude/upgrade/Diagnostics;
    //   230: pop
    //   231: aload 10
    //   233: astore 9
    //   235: aload_0
    //   236: invokespecial 164	com/amplitude/upgrade/DatabaseHelper:delete	()V
    //   239: lload 6
    //   241: lstore 4
    //   243: aload 10
    //   245: ifnull +22 -> 267
    //   248: aload 10
    //   250: invokevirtual 206	android/database/sqlite/SQLiteDatabase:isOpen	()Z
    //   253: istore 8
    //   255: lload 6
    //   257: lstore 4
    //   259: iload 8
    //   261: ifeq +6 -> 267
    //   264: goto -103 -> 161
    //   267: aload_0
    //   268: monitorexit
    //   269: lload 4
    //   271: lreturn
    //   272: aload 9
    //   274: ifnull +18 -> 292
    //   277: aload 9
    //   279: invokevirtual 206	android/database/sqlite/SQLiteDatabase:isOpen	()Z
    //   282: ifeq +10 -> 292
    //   285: aload_0
    //   286: invokevirtual 144	android/database/sqlite/SQLiteOpenHelper:close	()V
    //   289: goto +3 -> 292
    //   292: aload_1
    //   293: athrow
    //   294: aload_0
    //   295: monitorexit
    //   296: aload_1
    //   297: athrow
    //   298: astore_1
    //   299: goto -5 -> 294
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	302	0	this	DatabaseHelper
    //   0	302	1	paramString1	String
    //   0	302	2	paramString2	String
    //   0	302	3	paramObject	Object
    //   31	239	4	l1	long
    //   5	251	6	l2	long
    //   253	7	8	bool	boolean
    //   14	264	9	localObject1	Object
    //   20	229	10	localObject2	Object
    //   8	67	11	localObject3	Object
    //   11	163	12	localObject4	Object
    // Exception table:
    //   from	to	target	type
    //   22	33	53	java/lang/Throwable
    //   22	33	61	java/lang/StackOverflowError
    //   22	33	65	android/database/sqlite/SQLiteException
    //   16	22	69	java/lang/Throwable
    //   82	106	69	java/lang/Throwable
    //   110	132	69	java/lang/Throwable
    //   136	140	69	java/lang/Throwable
    //   181	205	69	java/lang/Throwable
    //   209	231	69	java/lang/Throwable
    //   235	239	69	java/lang/Throwable
    //   16	22	73	java/lang/StackOverflowError
    //   16	22	172	android/database/sqlite/SQLiteException
    //   38	50	298	java/lang/Throwable
    //   153	161	298	java/lang/Throwable
    //   161	165	298	java/lang/Throwable
    //   248	255	298	java/lang/Throwable
    //   277	289	298	java/lang/Throwable
    //   292	294	298	java/lang/Throwable
  }
  
  public void onCreate(SQLiteDatabase paramSQLiteDatabase)
  {
    paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS store (key TEXT PRIMARY KEY NOT NULL, value TEXT);");
    paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS long_store (key TEXT PRIMARY KEY NOT NULL, value INTEGER);");
    paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);");
    paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS identifys (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);");
    if ((databaseResetListener != null) && (callResetListenerOnDatabaseReset))
    {
      callResetListenerOnDatabaseReset = false;
      DatabaseResetListener localDatabaseResetListener = databaseResetListener;
      try
      {
        localDatabaseResetListener.onDatabaseReset(paramSQLiteDatabase);
        callResetListenerOnDatabaseReset = true;
        return;
      }
      catch (Throwable paramSQLiteDatabase) {}catch (SQLiteException paramSQLiteDatabase)
      {
        for (;;)
        {
          logger.e("com.amplitude.api.DatabaseHelper", String.format("databaseReset callback failed during onCreate", new Object[0]), paramSQLiteDatabase);
          Diagnostics.getLogger().logError(String.format("DB: Failed to run databaseReset callback during onCreate", new Object[0]), paramSQLiteDatabase);
        }
      }
      callResetListenerOnDatabaseReset = true;
      throw paramSQLiteDatabase;
    }
  }
  
  public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2)
  {
    if (paramInt1 > paramInt2)
    {
      logger.e("com.amplitude.api.DatabaseHelper", "onUpgrade() with invalid oldVersion and newVersion");
      resetDatabase(paramSQLiteDatabase);
      return;
    }
    if (paramInt2 <= 1) {
      return;
    }
    switch (paramInt1)
    {
    default: 
      AmplitudeLog localAmplitudeLog = logger;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("onUpgrade() with unknown oldVersion ");
      localStringBuilder.append(paramInt1);
      localAmplitudeLog.e("com.amplitude.api.DatabaseHelper", localStringBuilder.toString());
      resetDatabase(paramSQLiteDatabase);
      return;
    case 1: 
      paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS store (key TEXT PRIMARY KEY NOT NULL, value TEXT);");
      if (paramInt2 <= 2) {
        return;
      }
    case 2: 
      paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS identifys (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);");
      paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS long_store (key TEXT PRIMARY KEY NOT NULL, value INTEGER);");
    }
  }
  
  Cursor queryDb(SQLiteDatabase paramSQLiteDatabase, String paramString1, String[] paramArrayOfString1, String paramString2, String[] paramArrayOfString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    return paramSQLiteDatabase.query(paramString1, paramArrayOfString1, paramString2, paramArrayOfString2, paramString3, paramString4, paramString5, paramString6);
  }
  
  void removeEvent(long paramLong)
  {
    try
    {
      removeEventFromTable("events", paramLong);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void removeEvents(long paramLong)
  {
    try
    {
      removeEventsFromTable("events", paramLong);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void removeIdentify(long paramLong)
  {
    try
    {
      removeEventFromTable("identifys", paramLong);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void removeIdentifys(long paramLong)
  {
    try
    {
      removeEventsFromTable("identifys", paramLong);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  void setDatabaseResetListener(DatabaseResetListener paramDatabaseResetListener)
  {
    databaseResetListener = paramDatabaseResetListener;
  }
}

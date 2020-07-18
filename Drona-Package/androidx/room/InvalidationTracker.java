package androidx.room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.collection.ArrayMap;
import androidx.collection.ArraySet;
import androidx.collection.SimpleArrayMap;
import androidx.lifecycle.LiveData;
import androidx.sqlite.wiki.SimpleSQLiteQuery;
import androidx.sqlite.wiki.SupportSQLiteDatabase;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper;
import androidx.sqlite.wiki.SupportSQLiteStatement;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

public class InvalidationTracker
{
  private static final String CREATE_TRACKING_TABLE_SQL = "CREATE TEMP TABLE room_table_modification_log(table_id INTEGER PRIMARY KEY, invalidated INTEGER NOT NULL DEFAULT 0)";
  private static final String INVALIDATED_COLUMN_NAME = "invalidated";
  @VisibleForTesting
  static final String RESET_UPDATED_TABLES_SQL = "UPDATE room_table_modification_log SET invalidated = 0 WHERE invalidated = 1 ";
  @VisibleForTesting
  static final String SELECT_UPDATED_TABLES_SQL = "SELECT * FROM room_table_modification_log WHERE invalidated = 1;";
  private static final String TABLE_ID_COLUMN_NAME = "table_id";
  private static final String[] TRIGGERS = { "UPDATE", "DELETE", "INSERT" };
  private static final String UPDATE_TABLE_NAME = "room_table_modification_log";
  volatile SupportSQLiteStatement mCleanupStatement;
  final RoomDatabase mDatabase;
  private volatile boolean mInitialized;
  private final InvalidationLiveDataContainer mInvalidationLiveDataContainer;
  private MultiInstanceInvalidationClient mMultiInstanceInvalidationClient;
  private ObservedTableTracker mObservedTableTracker;
  @SuppressLint({"RestrictedApi"})
  @VisibleForTesting
  final SafeIterableMap<Observer, ObserverWrapper> mObserverMap;
  AtomicBoolean mPendingRefresh;
  @VisibleForTesting
  Runnable mRefreshRunnable;
  @NonNull
  @VisibleForTesting
  final ArrayMap<String, Integer> mTableIdLookup;
  final String[] mTableNames;
  @NonNull
  private Map<String, Set<String>> mViewTables;
  
  public InvalidationTracker(RoomDatabase paramRoomDatabase, Map paramMap1, Map paramMap2, String... paramVarArgs)
  {
    int i = 0;
    mPendingRefresh = new AtomicBoolean(false);
    mInitialized = false;
    mObserverMap = new SafeIterableMap();
    mRefreshRunnable = new Runnable()
    {
      private Set checkUpdatedTable()
      {
        ArraySet localArraySet = new ArraySet();
        Cursor localCursor = mDatabase.query(new SimpleSQLiteQuery("SELECT * FROM room_table_modification_log WHERE invalidated = 1;"));
        try
        {
          for (;;)
          {
            boolean bool = localCursor.moveToNext();
            if (!bool) {
              break;
            }
            localArraySet.add(Integer.valueOf(localCursor.getInt(0)));
          }
          localCursor.close();
          if (!localArraySet.isEmpty())
          {
            mCleanupStatement.executeUpdateDelete();
            return localArraySet;
          }
        }
        catch (Throwable localThrowable)
        {
          localCursor.close();
          throw localThrowable;
        }
        return localThrowable;
      }
      
      /* Error */
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   4: getfield 29	androidx/room/InvalidationTracker:mDatabase	Landroidx/room/RoomDatabase;
        //   7: invokevirtual 87	androidx/room/RoomDatabase:getCloseLock	()Ljava/util/concurrent/locks/Lock;
        //   10: astore 5
        //   12: aload 5
        //   14: invokeinterface 92 1 0
        //   19: aload_0
        //   20: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   23: astore_2
        //   24: aload_2
        //   25: invokevirtual 95	androidx/room/InvalidationTracker:ensureInitialization	()Z
        //   28: istore_1
        //   29: iload_1
        //   30: ifne +11 -> 41
        //   33: aload 5
        //   35: invokeinterface 98 1 0
        //   40: return
        //   41: aload_0
        //   42: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   45: getfield 102	androidx/room/InvalidationTracker:mPendingRefresh	Ljava/util/concurrent/atomic/AtomicBoolean;
        //   48: astore_2
        //   49: aload_2
        //   50: iconst_1
        //   51: iconst_0
        //   52: invokevirtual 108	java/util/concurrent/atomic/AtomicBoolean:compareAndSet	(ZZ)Z
        //   55: istore_1
        //   56: iload_1
        //   57: ifne +11 -> 68
        //   60: aload 5
        //   62: invokeinterface 98 1 0
        //   67: return
        //   68: aload_0
        //   69: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   72: getfield 29	androidx/room/InvalidationTracker:mDatabase	Landroidx/room/RoomDatabase;
        //   75: astore_2
        //   76: aload_2
        //   77: invokevirtual 111	androidx/room/RoomDatabase:inTransaction	()Z
        //   80: istore_1
        //   81: iload_1
        //   82: ifeq +11 -> 93
        //   85: aload 5
        //   87: invokeinterface 98 1 0
        //   92: return
        //   93: aload_0
        //   94: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   97: getfield 29	androidx/room/InvalidationTracker:mDatabase	Landroidx/room/RoomDatabase;
        //   100: getfield 115	androidx/room/RoomDatabase:mWriteAheadLoggingEnabled	Z
        //   103: istore_1
        //   104: iload_1
        //   105: ifeq +82 -> 187
        //   108: aload_0
        //   109: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   112: getfield 29	androidx/room/InvalidationTracker:mDatabase	Landroidx/room/RoomDatabase;
        //   115: astore_2
        //   116: aload_2
        //   117: invokevirtual 119	androidx/room/RoomDatabase:getOpenHelper	()Landroidx/sqlite/wiki/SupportSQLiteOpenHelper;
        //   120: invokeinterface 125 1 0
        //   125: astore 6
        //   127: aload 6
        //   129: invokeinterface 130 1 0
        //   134: aload_0
        //   135: invokespecial 132	androidx/room/InvalidationTracker$1:checkUpdatedTable	()Ljava/util/Set;
        //   138: astore_3
        //   139: aload 6
        //   141: invokeinterface 135 1 0
        //   146: aload_3
        //   147: astore_2
        //   148: aload 6
        //   150: invokeinterface 138 1 0
        //   155: aload_3
        //   156: astore_2
        //   157: goto +57 -> 214
        //   160: astore 4
        //   162: goto +7 -> 169
        //   165: astore 4
        //   167: aconst_null
        //   168: astore_3
        //   169: aload_3
        //   170: astore_2
        //   171: aload 6
        //   173: invokeinterface 138 1 0
        //   178: aload_3
        //   179: astore_2
        //   180: aload 4
        //   182: athrow
        //   183: astore_3
        //   184: goto +18 -> 202
        //   187: aload_0
        //   188: invokespecial 132	androidx/room/InvalidationTracker$1:checkUpdatedTable	()Ljava/util/Set;
        //   191: astore_2
        //   192: goto +22 -> 214
        //   195: astore_2
        //   196: goto +103 -> 299
        //   199: astore_3
        //   200: aconst_null
        //   201: astore_2
        //   202: ldc -116
        //   204: ldc -114
        //   206: aload_3
        //   207: checkcast 22	java/lang/Throwable
        //   210: invokestatic 148	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I
        //   213: pop
        //   214: aload 5
        //   216: invokeinterface 98 1 0
        //   221: aload_2
        //   222: ifnull +86 -> 308
        //   225: aload_2
        //   226: invokeinterface 151 1 0
        //   231: ifne +77 -> 308
        //   234: aload_0
        //   235: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   238: getfield 155	androidx/room/InvalidationTracker:mObserverMap	Landroidx/arch/core/internal/SafeIterableMap;
        //   241: astore_3
        //   242: aload_3
        //   243: monitorenter
        //   244: aload_0
        //   245: getfield 14	androidx/room/InvalidationTracker$1:this$0	Landroidx/room/InvalidationTracker;
        //   248: getfield 155	androidx/room/InvalidationTracker:mObserverMap	Landroidx/arch/core/internal/SafeIterableMap;
        //   251: invokevirtual 161	androidx/arch/core/internal/SafeIterableMap:iterator	()Ljava/util/Iterator;
        //   254: astore 4
        //   256: aload 4
        //   258: invokeinterface 166 1 0
        //   263: ifeq +28 -> 291
        //   266: aload 4
        //   268: invokeinterface 170 1 0
        //   273: checkcast 172	java/util/Map$Entry
        //   276: invokeinterface 175 1 0
        //   281: checkcast 177	androidx/room/InvalidationTracker$ObserverWrapper
        //   284: aload_2
        //   285: invokevirtual 181	androidx/room/InvalidationTracker$ObserverWrapper:notifyByTableInvalidStatus	(Ljava/util/Set;)V
        //   288: goto -32 -> 256
        //   291: aload_3
        //   292: monitorexit
        //   293: return
        //   294: astore_2
        //   295: aload_3
        //   296: monitorexit
        //   297: aload_2
        //   298: athrow
        //   299: aload 5
        //   301: invokeinterface 98 1 0
        //   306: aload_2
        //   307: athrow
        //   308: return
        // Local variable table:
        //   start	length	slot	name	signature
        //   0	309	0	this	1
        //   28	77	1	bool	boolean
        //   23	169	2	localObject	Object
        //   195	1	2	localThrowable1	Throwable
        //   201	84	2	localSet1	Set
        //   294	13	2	localThrowable2	Throwable
        //   138	41	3	localSet2	Set
        //   183	1	3	localIllegalStateException1	IllegalStateException
        //   199	8	3	localIllegalStateException2	IllegalStateException
        //   241	55	3	localSafeIterableMap	SafeIterableMap
        //   160	1	4	localThrowable3	Throwable
        //   165	16	4	localThrowable4	Throwable
        //   254	13	4	localIterator	Iterator
        //   10	290	5	localLock	Lock
        //   125	47	6	localSupportSQLiteDatabase	SupportSQLiteDatabase
        // Exception table:
        //   from	to	target	type
        //   139	146	160	java/lang/Throwable
        //   134	139	165	java/lang/Throwable
        //   148	155	183	java/lang/IllegalStateException
        //   148	155	183	android/database/sqlite/SQLiteException
        //   171	178	183	java/lang/IllegalStateException
        //   171	178	183	android/database/sqlite/SQLiteException
        //   180	183	183	java/lang/IllegalStateException
        //   180	183	183	android/database/sqlite/SQLiteException
        //   12	19	195	java/lang/Throwable
        //   24	29	195	java/lang/Throwable
        //   49	56	195	java/lang/Throwable
        //   76	81	195	java/lang/Throwable
        //   93	104	195	java/lang/Throwable
        //   116	134	195	java/lang/Throwable
        //   148	155	195	java/lang/Throwable
        //   171	178	195	java/lang/Throwable
        //   180	183	195	java/lang/Throwable
        //   187	192	195	java/lang/Throwable
        //   202	214	195	java/lang/Throwable
        //   12	19	199	java/lang/IllegalStateException
        //   12	19	199	android/database/sqlite/SQLiteException
        //   24	29	199	java/lang/IllegalStateException
        //   24	29	199	android/database/sqlite/SQLiteException
        //   49	56	199	java/lang/IllegalStateException
        //   49	56	199	android/database/sqlite/SQLiteException
        //   76	81	199	java/lang/IllegalStateException
        //   76	81	199	android/database/sqlite/SQLiteException
        //   116	134	199	java/lang/IllegalStateException
        //   116	134	199	android/database/sqlite/SQLiteException
        //   187	192	199	java/lang/IllegalStateException
        //   187	192	199	android/database/sqlite/SQLiteException
        //   244	256	294	java/lang/Throwable
        //   256	288	294	java/lang/Throwable
        //   291	293	294	java/lang/Throwable
        //   295	297	294	java/lang/Throwable
      }
    };
    mDatabase = paramRoomDatabase;
    mObservedTableTracker = new ObservedTableTracker(paramVarArgs.length);
    mTableIdLookup = new ArrayMap();
    mViewTables = paramMap2;
    mInvalidationLiveDataContainer = new InvalidationLiveDataContainer(mDatabase);
    int j = paramVarArgs.length;
    mTableNames = new String[j];
    while (i < j)
    {
      paramRoomDatabase = paramVarArgs[i].toLowerCase(Locale.US);
      mTableIdLookup.put(paramRoomDatabase, Integer.valueOf(i));
      paramMap2 = (String)paramMap1.get(paramVarArgs[i]);
      if (paramMap2 != null) {
        mTableNames[i] = paramMap2.toLowerCase(Locale.US);
      } else {
        mTableNames[i] = paramRoomDatabase;
      }
      i += 1;
    }
    paramRoomDatabase = paramMap1.entrySet().iterator();
    while (paramRoomDatabase.hasNext())
    {
      paramMap2 = (Map.Entry)paramRoomDatabase.next();
      paramMap1 = ((String)paramMap2.getValue()).toLowerCase(Locale.US);
      if (mTableIdLookup.containsKey(paramMap1))
      {
        paramMap2 = ((String)paramMap2.getKey()).toLowerCase(Locale.US);
        mTableIdLookup.put(paramMap2, mTableIdLookup.get(paramMap1));
      }
    }
  }
  
  public InvalidationTracker(RoomDatabase paramRoomDatabase, String... paramVarArgs)
  {
    this(paramRoomDatabase, new HashMap(), Collections.emptyMap(), paramVarArgs);
  }
  
  private static void appendTriggerName(StringBuilder paramStringBuilder, String paramString1, String paramString2)
  {
    paramStringBuilder.append("`");
    paramStringBuilder.append("room_table_modification_trigger_");
    paramStringBuilder.append(paramString1);
    paramStringBuilder.append("_");
    paramStringBuilder.append(paramString2);
    paramStringBuilder.append("`");
  }
  
  private String[] resolveViews(String[] paramArrayOfString)
  {
    ArraySet localArraySet = new ArraySet();
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str1 = paramArrayOfString[i];
      String str2 = str1.toLowerCase(Locale.US);
      if (mViewTables.containsKey(str2)) {
        localArraySet.addAll((Collection)mViewTables.get(str2));
      } else {
        localArraySet.add(str1);
      }
      i += 1;
    }
    return (String[])localArraySet.toArray(new String[localArraySet.size()]);
  }
  
  private void startTrackingTable(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt)
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append("INSERT OR IGNORE INTO room_table_modification_log VALUES(");
    ((StringBuilder)localObject).append(paramInt);
    ((StringBuilder)localObject).append(", 0)");
    paramSupportSQLiteDatabase.execSQL(((StringBuilder)localObject).toString());
    localObject = mTableNames[paramInt];
    StringBuilder localStringBuilder = new StringBuilder();
    String[] arrayOfString = TRIGGERS;
    int j = arrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = arrayOfString[i];
      localStringBuilder.setLength(0);
      localStringBuilder.append("CREATE TEMP TRIGGER IF NOT EXISTS ");
      appendTriggerName(localStringBuilder, (String)localObject, str);
      localStringBuilder.append(" AFTER ");
      localStringBuilder.append(str);
      localStringBuilder.append(" ON `");
      localStringBuilder.append((String)localObject);
      localStringBuilder.append("` BEGIN UPDATE ");
      localStringBuilder.append("room_table_modification_log");
      localStringBuilder.append(" SET ");
      localStringBuilder.append("invalidated");
      localStringBuilder.append(" = 1");
      localStringBuilder.append(" WHERE ");
      localStringBuilder.append("table_id");
      localStringBuilder.append(" = ");
      localStringBuilder.append(paramInt);
      localStringBuilder.append(" AND ");
      localStringBuilder.append("invalidated");
      localStringBuilder.append(" = 0");
      localStringBuilder.append("; END");
      paramSupportSQLiteDatabase.execSQL(localStringBuilder.toString());
      i += 1;
    }
  }
  
  private void stopTrackingTable(SupportSQLiteDatabase paramSupportSQLiteDatabase, int paramInt)
  {
    String str1 = mTableNames[paramInt];
    StringBuilder localStringBuilder = new StringBuilder();
    String[] arrayOfString = TRIGGERS;
    int i = arrayOfString.length;
    paramInt = 0;
    while (paramInt < i)
    {
      String str2 = arrayOfString[paramInt];
      localStringBuilder.setLength(0);
      localStringBuilder.append("DROP TRIGGER IF EXISTS ");
      appendTriggerName(localStringBuilder, str1, str2);
      paramSupportSQLiteDatabase.execSQL(localStringBuilder.toString());
      paramInt += 1;
    }
  }
  
  private String[] validateAndResolveTableNames(String[] paramArrayOfString)
  {
    Object localObject = resolveViews(paramArrayOfString);
    int j = localObject.length;
    int i = 0;
    while (i < j)
    {
      paramArrayOfString = localObject[i];
      if (mTableIdLookup.containsKey(paramArrayOfString.toLowerCase(Locale.US)))
      {
        i += 1;
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("There is no table with name ");
        ((StringBuilder)localObject).append(paramArrayOfString);
        throw new IllegalArgumentException(((StringBuilder)localObject).toString());
      }
    }
    return localObject;
  }
  
  public void addObserver(Observer paramObserver)
  {
    Object localObject1 = resolveViews(mTables);
    int[] arrayOfInt = new int[localObject1.length];
    int j = localObject1.length;
    int i = 0;
    while (i < j)
    {
      localObject2 = (Integer)mTableIdLookup.get(localObject1[i].toLowerCase(Locale.US));
      if (localObject2 != null)
      {
        arrayOfInt[i] = ((Integer)localObject2).intValue();
        i += 1;
      }
      else
      {
        paramObserver = new StringBuilder();
        paramObserver.append("There is no table with name ");
        paramObserver.append(localObject1[i]);
        throw new IllegalArgumentException(paramObserver.toString());
      }
    }
    Object localObject2 = new ObserverWrapper(paramObserver, arrayOfInt, (String[])localObject1);
    localObject1 = mObserverMap;
    try
    {
      paramObserver = (ObserverWrapper)mObserverMap.putIfAbsent(paramObserver, localObject2);
      if ((paramObserver == null) && (mObservedTableTracker.onAdded(arrayOfInt)))
      {
        syncTriggers();
        return;
      }
    }
    catch (Throwable paramObserver)
    {
      throw paramObserver;
    }
  }
  
  public void addWeakObserver(Observer paramObserver)
  {
    addObserver(new WeakObserver(this, paramObserver));
  }
  
  public LiveData createLiveData(String[] paramArrayOfString, Callable paramCallable)
  {
    return createLiveData(paramArrayOfString, false, paramCallable);
  }
  
  public LiveData createLiveData(String[] paramArrayOfString, boolean paramBoolean, Callable paramCallable)
  {
    return mInvalidationLiveDataContainer.create(validateAndResolveTableNames(paramArrayOfString), paramBoolean, paramCallable);
  }
  
  boolean ensureInitialization()
  {
    if (!mDatabase.isOpen()) {
      return false;
    }
    if (!mInitialized) {
      mDatabase.getOpenHelper().getWritableDatabase();
    }
    if (!mInitialized)
    {
      Log.e("ROOM", "database is not initialized even though it is open");
      return false;
    }
    return true;
  }
  
  void internalInit(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    try
    {
      if (mInitialized)
      {
        Log.e("ROOM", "Invalidation tracker is initialized twice :/.");
        return;
      }
      paramSupportSQLiteDatabase.execSQL("PRAGMA temp_store = MEMORY;");
      paramSupportSQLiteDatabase.execSQL("PRAGMA recursive_triggers='ON';");
      paramSupportSQLiteDatabase.execSQL("CREATE TEMP TABLE room_table_modification_log(table_id INTEGER PRIMARY KEY, invalidated INTEGER NOT NULL DEFAULT 0)");
      syncTriggers(paramSupportSQLiteDatabase);
      mCleanupStatement = paramSupportSQLiteDatabase.compileStatement("UPDATE room_table_modification_log SET invalidated = 0 WHERE invalidated = 1 ");
      mInitialized = true;
      return;
    }
    catch (Throwable paramSupportSQLiteDatabase)
    {
      throw paramSupportSQLiteDatabase;
    }
  }
  
  public void notifyObserversByTableNames(String... paramVarArgs)
  {
    SafeIterableMap localSafeIterableMap = mObserverMap;
    try
    {
      Iterator localIterator = mObserverMap.iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        if (!((Observer)localEntry.getKey()).isRemote()) {
          ((ObserverWrapper)localEntry.getValue()).notifyByTableNames(paramVarArgs);
        }
      }
      return;
    }
    catch (Throwable paramVarArgs)
    {
      throw paramVarArgs;
    }
  }
  
  public void refreshVersionsAsync()
  {
    if (mPendingRefresh.compareAndSet(false, true)) {
      mDatabase.getQueryExecutor().execute(mRefreshRunnable);
    }
  }
  
  public void refreshVersionsSync()
  {
    syncTriggers();
    mRefreshRunnable.run();
  }
  
  public void removeObserver(Observer paramObserver)
  {
    SafeIterableMap localSafeIterableMap = mObserverMap;
    try
    {
      paramObserver = (ObserverWrapper)mObserverMap.remove(paramObserver);
      if ((paramObserver != null) && (mObservedTableTracker.onRemoved(mTableIds)))
      {
        syncTriggers();
        return;
      }
    }
    catch (Throwable paramObserver)
    {
      throw paramObserver;
    }
  }
  
  void startMultiInstanceInvalidation(Context paramContext, String paramString)
  {
    mMultiInstanceInvalidationClient = new MultiInstanceInvalidationClient(paramContext, paramString, this, mDatabase.getQueryExecutor());
  }
  
  void stopMultiInstanceInvalidation()
  {
    if (mMultiInstanceInvalidationClient != null)
    {
      mMultiInstanceInvalidationClient.stop();
      mMultiInstanceInvalidationClient = null;
    }
  }
  
  void syncTriggers()
  {
    if (!mDatabase.isOpen()) {
      return;
    }
    syncTriggers(mDatabase.getOpenHelper().getWritableDatabase());
  }
  
  void syncTriggers(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    if (paramSupportSQLiteDatabase.inTransaction()) {
      return;
    }
    for (;;)
    {
      Object localObject = mDatabase;
      try
      {
        localObject = ((RoomDatabase)localObject).getCloseLock();
        ((Lock)localObject).lock();
        try
        {
          int[] arrayOfInt = mObservedTableTracker.getTablesToSync();
          if (arrayOfInt == null)
          {
            ((Lock)localObject).unlock();
            return;
          }
          int j = arrayOfInt.length;
          paramSupportSQLiteDatabase.beginTransaction();
          int i = 0;
          for (;;)
          {
            if (i < j)
            {
              switch (arrayOfInt[i])
              {
              default: 
                break;
              }
              try
              {
                stopTrackingTable(paramSupportSQLiteDatabase, i);
                break label121;
                startTrackingTable(paramSupportSQLiteDatabase, i);
                label121:
                i += 1;
              }
              catch (Throwable localThrowable)
              {
                break label162;
              }
            }
          }
          paramSupportSQLiteDatabase.setTransactionSuccessful();
          paramSupportSQLiteDatabase.endTransaction();
          mObservedTableTracker.onSyncCompleted();
          ((Lock)localObject).unlock();
        }
        catch (Throwable paramSupportSQLiteDatabase)
        {
          label162:
          ((Lock)localObject).unlock();
          throw paramSupportSQLiteDatabase;
        }
        paramSupportSQLiteDatabase.endTransaction();
        throw localThrowable;
      }
      catch (IllegalStateException|SQLiteException paramSupportSQLiteDatabase)
      {
        Log.e("ROOM", "Cannot run invalidation tracker. Is the db closed?", paramSupportSQLiteDatabase);
      }
    }
  }
  
  static class ObservedTableTracker
  {
    static final int NO_OP = 0;
    static final int REMOVE = 2;
    static final int TYPE_EXPANDED = 1;
    boolean mNeedsSync;
    boolean mPendingSync;
    final long[] mTableObservers;
    final int[] mTriggerStateChanges;
    final boolean[] mTriggerStates;
    
    ObservedTableTracker(int paramInt)
    {
      mTableObservers = new long[paramInt];
      mTriggerStates = new boolean[paramInt];
      mTriggerStateChanges = new int[paramInt];
      Arrays.fill(mTableObservers, 0L);
      Arrays.fill(mTriggerStates, false);
    }
    
    int[] getTablesToSync()
    {
      Object localObject = this;
      for (;;)
      {
        int i;
        int[] arrayOfInt;
        try
        {
          int m = mNeedsSync;
          ObservedTableTracker localObservedTableTracker = this;
          if (m != 0)
          {
            localObject = localObservedTableTracker;
            m = mPendingSync;
            if (m == 0)
            {
              localObject = localObservedTableTracker;
              int k = mTableObservers.length;
              i = 0;
              j = 1;
              if (i < k)
              {
                localObject = localObservedTableTracker;
                if (mTableObservers[i] <= 0L) {
                  break label208;
                }
                m = 1;
                localObject = localObservedTableTracker;
                if (m != mTriggerStates[i])
                {
                  localObject = localObservedTableTracker;
                  arrayOfInt = mTriggerStateChanges;
                  if (m == 0) {
                    break label214;
                  }
                  break label216;
                }
                localObject = localObservedTableTracker;
                mTriggerStateChanges[i] = 0;
                localObject = localObservedTableTracker;
                mTriggerStates[i] = m;
                i += 1;
                continue;
              }
              localObject = localObservedTableTracker;
              mPendingSync = true;
              localObject = localObservedTableTracker;
              mNeedsSync = false;
              localObject = localObservedTableTracker;
              arrayOfInt = mTriggerStateChanges;
              localObject = localObservedTableTracker;
              return arrayOfInt;
            }
          }
          localObservedTableTracker = this;
          localObject = localObservedTableTracker;
          return null;
        }
        catch (Throwable localThrowable)
        {
          throw localThrowable;
        }
        label208:
        int n = 0;
        continue;
        label214:
        int j = 2;
        label216:
        arrayOfInt[i] = j;
      }
    }
    
    boolean onAdded(int... paramVarArgs)
    {
      for (;;)
      {
        int i;
        try
        {
          int j = paramVarArgs.length;
          i = 0;
          boolean bool = false;
          if (i < j)
          {
            int k = paramVarArgs[i];
            long l = mTableObservers[k];
            mTableObservers[k] = (1L + l);
            if (l == 0L)
            {
              mNeedsSync = true;
              bool = true;
            }
          }
          else
          {
            return bool;
          }
        }
        catch (Throwable paramVarArgs)
        {
          throw paramVarArgs;
        }
        i += 1;
      }
    }
    
    boolean onRemoved(int... paramVarArgs)
    {
      for (;;)
      {
        int i;
        try
        {
          int j = paramVarArgs.length;
          i = 0;
          boolean bool = false;
          if (i < j)
          {
            int k = paramVarArgs[i];
            long l = mTableObservers[k];
            mTableObservers[k] = (l - 1L);
            if (l == 1L)
            {
              mNeedsSync = true;
              bool = true;
            }
          }
          else
          {
            return bool;
          }
        }
        catch (Throwable paramVarArgs)
        {
          throw paramVarArgs;
        }
        i += 1;
      }
    }
    
    void onSyncCompleted()
    {
      try
      {
        mPendingSync = false;
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  public static abstract class Observer
  {
    final String[] mTables;
    
    protected Observer(String paramString, String... paramVarArgs)
    {
      mTables = ((String[])Arrays.copyOf(paramVarArgs, paramVarArgs.length + 1));
      mTables[paramVarArgs.length] = paramString;
    }
    
    public Observer(String[] paramArrayOfString)
    {
      mTables = ((String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length));
    }
    
    boolean isRemote()
    {
      return false;
    }
    
    public abstract void onInvalidated(Set paramSet);
  }
  
  static class ObserverWrapper
  {
    final InvalidationTracker.Observer mObserver;
    private final Set<String> mSingleTableSet;
    final int[] mTableIds;
    private final String[] mTableNames;
    
    ObserverWrapper(InvalidationTracker.Observer paramObserver, int[] paramArrayOfInt, String[] paramArrayOfString)
    {
      mObserver = paramObserver;
      mTableIds = paramArrayOfInt;
      mTableNames = paramArrayOfString;
      if (paramArrayOfInt.length == 1)
      {
        paramObserver = new ArraySet();
        paramObserver.add(mTableNames[0]);
        mSingleTableSet = Collections.unmodifiableSet(paramObserver);
        return;
      }
      mSingleTableSet = null;
    }
    
    void notifyByTableInvalidStatus(Set paramSet)
    {
      throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a14 = a13\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
    }
    
    void notifyByTableNames(String[] paramArrayOfString)
    {
      Object localObject1 = mTableNames;
      ObserverWrapper localObserverWrapper = this;
      int i = localObject1.length;
      Object localObject3 = null;
      int j;
      if (i == 1)
      {
        j = paramArrayOfString.length;
        i = 0;
        for (;;)
        {
          localObject1 = localObject3;
          localObject2 = localObserverWrapper;
          if (i >= j) {
            break;
          }
          localObject1 = paramArrayOfString[i];
          localObject2 = mTableNames;
          if (((String)localObject1).equalsIgnoreCase(localObject2[0]))
          {
            localObject1 = mSingleTableSet;
            localObject2 = localObserverWrapper;
            break;
          }
          i += 1;
        }
      }
      ArraySet localArraySet = new ArraySet();
      int k = paramArrayOfString.length;
      i = 0;
      while (i < k)
      {
        localObject1 = paramArrayOfString[i];
        localObject2 = mTableNames;
        int m = localObject2.length;
        j = 0;
        while (j < m)
        {
          Object localObject4 = localObject2[j];
          if (localObject4.equalsIgnoreCase((String)localObject1))
          {
            localArraySet.add(localObject4);
            break;
          }
          j += 1;
        }
        i += 1;
      }
      localObject1 = localObject3;
      Object localObject2 = localObserverWrapper;
      if (localArraySet.size() > 0)
      {
        localObject1 = localArraySet;
        localObject2 = localObserverWrapper;
      }
      if (localObject1 != null) {
        mObserver.onInvalidated((Set)localObject1);
      }
    }
  }
  
  static class WeakObserver
    extends InvalidationTracker.Observer
  {
    final WeakReference<InvalidationTracker.Observer> mDelegateRef;
    final InvalidationTracker mTracker;
    
    WeakObserver(InvalidationTracker paramInvalidationTracker, InvalidationTracker.Observer paramObserver)
    {
      super();
      mTracker = paramInvalidationTracker;
      mDelegateRef = new WeakReference(paramObserver);
    }
    
    public void onInvalidated(Set paramSet)
    {
      InvalidationTracker.Observer localObserver = (InvalidationTracker.Observer)mDelegateRef.get();
      if (localObserver == null)
      {
        mTracker.removeObserver(this);
        return;
      }
      localObserver.onInvalidated(paramSet);
    }
  }
}

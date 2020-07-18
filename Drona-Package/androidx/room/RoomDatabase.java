package androidx.room;

import android.app.ActivityManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Build.VERSION;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RestrictTo;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.collection.SparseArrayCompat;
import androidx.core.package_4.ActivityManagerCompat;
import androidx.room.migration.Migration;
import androidx.room.util.SneakyThrow;
import androidx.sqlite.wiki.SimpleSQLiteQuery;
import androidx.sqlite.wiki.SupportSQLiteDatabase;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper;
import androidx.sqlite.wiki.SupportSQLiteOpenHelper.Factory;
import androidx.sqlite.wiki.SupportSQLiteQuery;
import androidx.sqlite.wiki.SupportSQLiteStatement;
import androidx.sqlite.wiki.framework.FrameworkSQLiteOpenHelperFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

public abstract class RoomDatabase
{
  private static final String DB_IMPL_SUFFIX = "_Impl";
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public static final int MAX_BIND_PARAMETER_CNT = 999;
  private boolean mAllowMainThreadQueries;
  private final Map<String, Object> mBackingFieldMap = new ConcurrentHashMap();
  @Deprecated
  @Nullable
  protected List<Callback> mCallbacks;
  private final ReentrantReadWriteLock mCloseLock = new ReentrantReadWriteLock();
  @Deprecated
  protected volatile SupportSQLiteDatabase mDatabase;
  private final InvalidationTracker mInvalidationTracker = createInvalidationTracker();
  private SupportSQLiteOpenHelper mOpenHelper;
  private Executor mQueryExecutor;
  private final ThreadLocal<Integer> mSuspendingTransactionId = new ThreadLocal();
  private Executor mTransactionExecutor;
  boolean mWriteAheadLoggingEnabled;
  
  public RoomDatabase() {}
  
  private static boolean isMainThread()
  {
    return Looper.getMainLooper().getThread() == Thread.currentThread();
  }
  
  public void assertNotMainThread()
  {
    if (mAllowMainThreadQueries) {
      return;
    }
    if (!isMainThread()) {
      return;
    }
    throw new IllegalStateException("Cannot access database on the main thread since it may potentially lock the UI for a long period of time.");
  }
  
  public void assertNotSuspendingTransaction()
  {
    if (!inTransaction())
    {
      if (mSuspendingTransactionId.get() == null) {
        return;
      }
      throw new IllegalStateException("Cannot access database on a different coroutine context inherited from a suspending transaction.");
    }
  }
  
  public void beginTransaction()
  {
    assertNotMainThread();
    SupportSQLiteDatabase localSupportSQLiteDatabase = mOpenHelper.getWritableDatabase();
    mInvalidationTracker.syncTriggers(localSupportSQLiteDatabase);
    localSupportSQLiteDatabase.beginTransaction();
  }
  
  public abstract void clearAllTables();
  
  public void close()
  {
    if (isOpen())
    {
      ReentrantReadWriteLock.WriteLock localWriteLock = mCloseLock.writeLock();
      try
      {
        localWriteLock.lock();
        mInvalidationTracker.stopMultiInstanceInvalidation();
        mOpenHelper.close();
        localWriteLock.unlock();
        return;
      }
      catch (Throwable localThrowable)
      {
        localWriteLock.unlock();
        throw localThrowable;
      }
    }
  }
  
  public SupportSQLiteStatement compileStatement(String paramString)
  {
    assertNotMainThread();
    assertNotSuspendingTransaction();
    return mOpenHelper.getWritableDatabase().compileStatement(paramString);
  }
  
  protected abstract InvalidationTracker createInvalidationTracker();
  
  protected abstract SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration paramDatabaseConfiguration);
  
  public void endTransaction()
  {
    mOpenHelper.getWritableDatabase().endTransaction();
    if (!inTransaction()) {
      mInvalidationTracker.refreshVersionsAsync();
    }
  }
  
  Map getBackingFieldMap()
  {
    return mBackingFieldMap;
  }
  
  Lock getCloseLock()
  {
    return mCloseLock.readLock();
  }
  
  public InvalidationTracker getInvalidationTracker()
  {
    return mInvalidationTracker;
  }
  
  public SupportSQLiteOpenHelper getOpenHelper()
  {
    return mOpenHelper;
  }
  
  public Executor getQueryExecutor()
  {
    return mQueryExecutor;
  }
  
  ThreadLocal getSuspendingTransactionId()
  {
    return mSuspendingTransactionId;
  }
  
  public Executor getTransactionExecutor()
  {
    return mTransactionExecutor;
  }
  
  public boolean inTransaction()
  {
    return mOpenHelper.getWritableDatabase().inTransaction();
  }
  
  public void init(DatabaseConfiguration paramDatabaseConfiguration)
  {
    mOpenHelper = createOpenHelper(paramDatabaseConfiguration);
    int i = Build.VERSION.SDK_INT;
    boolean bool1 = false;
    boolean bool2 = false;
    if (i >= 16)
    {
      bool1 = bool2;
      if (journalMode == JournalMode.WRITE_AHEAD_LOGGING) {
        bool1 = true;
      }
      mOpenHelper.setWriteAheadLoggingEnabled(bool1);
    }
    mCallbacks = callbacks;
    mQueryExecutor = queryExecutor;
    mTransactionExecutor = new TransactionExecutor(transactionExecutor);
    mAllowMainThreadQueries = allowMainThreadQueries;
    mWriteAheadLoggingEnabled = bool1;
    if (multiInstanceInvalidation) {
      mInvalidationTracker.startMultiInstanceInvalidation(context, name);
    }
  }
  
  protected void internalInitInvalidationTracker(SupportSQLiteDatabase paramSupportSQLiteDatabase)
  {
    mInvalidationTracker.internalInit(paramSupportSQLiteDatabase);
  }
  
  public boolean isOpen()
  {
    SupportSQLiteDatabase localSupportSQLiteDatabase = mDatabase;
    return (localSupportSQLiteDatabase != null) && (localSupportSQLiteDatabase.isOpen());
  }
  
  public Cursor query(SupportSQLiteQuery paramSupportSQLiteQuery)
  {
    assertNotMainThread();
    assertNotSuspendingTransaction();
    return mOpenHelper.getWritableDatabase().query(paramSupportSQLiteQuery);
  }
  
  public Cursor query(String paramString, Object[] paramArrayOfObject)
  {
    return mOpenHelper.getWritableDatabase().query(new SimpleSQLiteQuery(paramString, paramArrayOfObject));
  }
  
  public Object runInTransaction(Callable paramCallable)
  {
    beginTransaction();
    try
    {
      paramCallable = paramCallable.call();
      setTransactionSuccessful();
      endTransaction();
      return paramCallable;
    }
    catch (Throwable paramCallable) {}catch (Exception paramCallable)
    {
      SneakyThrow.reThrow(paramCallable);
      endTransaction();
      return null;
    }
    catch (RuntimeException paramCallable)
    {
      throw paramCallable;
    }
    endTransaction();
    throw paramCallable;
  }
  
  public void runInTransaction(Runnable paramRunnable)
  {
    beginTransaction();
    try
    {
      paramRunnable.run();
      setTransactionSuccessful();
      endTransaction();
      return;
    }
    catch (Throwable paramRunnable)
    {
      endTransaction();
      throw paramRunnable;
    }
  }
  
  public void setTransactionSuccessful()
  {
    mOpenHelper.getWritableDatabase().setTransactionSuccessful();
  }
  
  public static class Builder<T extends RoomDatabase>
  {
    private boolean mAllowDestructiveMigrationOnDowngrade;
    private boolean mAllowMainThreadQueries;
    private ArrayList<RoomDatabase.Callback> mCallbacks;
    private final Context mContext;
    private final Class<T> mDatabaseClass;
    private SupportSQLiteOpenHelper.Factory mFactory;
    private RoomDatabase.JournalMode mJournalMode;
    private final RoomDatabase.MigrationContainer mMigrationContainer;
    private Set<Integer> mMigrationStartAndEndVersions;
    private Set<Integer> mMigrationsNotRequiredFrom;
    private boolean mMultiInstanceInvalidation;
    private final String mName;
    private Executor mQueryExecutor;
    private boolean mRequireMigration;
    private Executor mTransactionExecutor;
    
    Builder(Context paramContext, Class paramClass, String paramString)
    {
      mContext = paramContext;
      mDatabaseClass = paramClass;
      mName = paramString;
      mJournalMode = RoomDatabase.JournalMode.AUTOMATIC;
      mRequireMigration = true;
      mMigrationContainer = new RoomDatabase.MigrationContainer();
    }
    
    public Builder addCallback(RoomDatabase.Callback paramCallback)
    {
      if (mCallbacks == null) {
        mCallbacks = new ArrayList();
      }
      mCallbacks.add(paramCallback);
      return this;
    }
    
    public Builder addMigrations(Migration... paramVarArgs)
    {
      if (mMigrationStartAndEndVersions == null) {
        mMigrationStartAndEndVersions = new HashSet();
      }
      int j = paramVarArgs.length;
      int i = 0;
      while (i < j)
      {
        Migration localMigration = paramVarArgs[i];
        mMigrationStartAndEndVersions.add(Integer.valueOf(startVersion));
        mMigrationStartAndEndVersions.add(Integer.valueOf(endVersion));
        i += 1;
      }
      mMigrationContainer.addMigrations(paramVarArgs);
      return this;
    }
    
    public Builder allowMainThreadQueries()
    {
      mAllowMainThreadQueries = true;
      return this;
    }
    
    public RoomDatabase build()
    {
      if (mContext != null)
      {
        if (mDatabaseClass != null)
        {
          if ((mQueryExecutor == null) && (mTransactionExecutor == null))
          {
            localObject1 = ArchTaskExecutor.getIOThreadExecutor();
            mTransactionExecutor = ((Executor)localObject1);
            mQueryExecutor = ((Executor)localObject1);
          }
          else if ((mQueryExecutor != null) && (mTransactionExecutor == null))
          {
            mTransactionExecutor = mQueryExecutor;
          }
          else if ((mQueryExecutor == null) && (mTransactionExecutor != null))
          {
            mQueryExecutor = mTransactionExecutor;
          }
          if ((mMigrationStartAndEndVersions != null) && (mMigrationsNotRequiredFrom != null))
          {
            localObject2 = mMigrationStartAndEndVersions.iterator();
            while (((Iterator)localObject2).hasNext())
            {
              localObject1 = (Integer)((Iterator)localObject2).next();
              if (mMigrationsNotRequiredFrom.contains(localObject1))
              {
                localObject2 = new StringBuilder();
                ((StringBuilder)localObject2).append("Inconsistency detected. A Migration was supplied to addMigration(Migration... migrations) that has a start or end version equal to a start version supplied to fallbackToDestructiveMigrationFrom(int... startVersions). Start version: ");
                ((StringBuilder)localObject2).append(localObject1);
                throw new IllegalArgumentException(((StringBuilder)localObject2).toString());
              }
            }
          }
          if (mFactory == null) {
            mFactory = new FrameworkSQLiteOpenHelperFactory();
          }
          Object localObject1 = new DatabaseConfiguration(mContext, mName, mFactory, mMigrationContainer, mCallbacks, mAllowMainThreadQueries, mJournalMode.resolve(mContext), mQueryExecutor, mTransactionExecutor, mMultiInstanceInvalidation, mRequireMigration, mAllowDestructiveMigrationOnDowngrade, mMigrationsNotRequiredFrom);
          Object localObject2 = (RoomDatabase)Room.getGeneratedImplementation(mDatabaseClass, "_Impl");
          ((RoomDatabase)localObject2).init((DatabaseConfiguration)localObject1);
          return localObject2;
        }
        throw new IllegalArgumentException("Must provide an abstract class that extends RoomDatabase");
      }
      throw new IllegalArgumentException("Cannot provide null context for the database.");
    }
    
    public Builder enableMultiInstanceInvalidation()
    {
      boolean bool;
      if (mName != null) {
        bool = true;
      } else {
        bool = false;
      }
      mMultiInstanceInvalidation = bool;
      return this;
    }
    
    public Builder fallbackToDestructiveMigration()
    {
      mRequireMigration = false;
      mAllowDestructiveMigrationOnDowngrade = true;
      return this;
    }
    
    public Builder fallbackToDestructiveMigrationFrom(int... paramVarArgs)
    {
      if (mMigrationsNotRequiredFrom == null) {
        mMigrationsNotRequiredFrom = new HashSet(paramVarArgs.length);
      }
      int j = paramVarArgs.length;
      int i = 0;
      while (i < j)
      {
        int k = paramVarArgs[i];
        mMigrationsNotRequiredFrom.add(Integer.valueOf(k));
        i += 1;
      }
      return this;
    }
    
    public Builder fallbackToDestructiveMigrationOnDowngrade()
    {
      mRequireMigration = true;
      mAllowDestructiveMigrationOnDowngrade = true;
      return this;
    }
    
    public Builder openHelperFactory(SupportSQLiteOpenHelper.Factory paramFactory)
    {
      mFactory = paramFactory;
      return this;
    }
    
    public Builder setJournalMode(RoomDatabase.JournalMode paramJournalMode)
    {
      mJournalMode = paramJournalMode;
      return this;
    }
    
    public Builder setQueryExecutor(Executor paramExecutor)
    {
      mQueryExecutor = paramExecutor;
      return this;
    }
    
    public Builder setTransactionExecutor(Executor paramExecutor)
    {
      mTransactionExecutor = paramExecutor;
      return this;
    }
  }
  
  public static abstract class Callback
  {
    public Callback() {}
    
    public void onCreate(SupportSQLiteDatabase paramSupportSQLiteDatabase) {}
    
    public void onOpen(SupportSQLiteDatabase paramSupportSQLiteDatabase) {}
  }
  
  public static enum JournalMode
  {
    AUTOMATIC,  TRUNCATE,  WRITE_AHEAD_LOGGING;
    
    JournalMode resolve(Context paramContext)
    {
      if (this != AUTOMATIC) {
        return this;
      }
      if (Build.VERSION.SDK_INT >= 16)
      {
        paramContext = (ActivityManager)paramContext.getSystemService("activity");
        if ((paramContext != null) && (!ActivityManagerCompat.isLowRamDevice(paramContext))) {
          return WRITE_AHEAD_LOGGING;
        }
      }
      return TRUNCATE;
    }
  }
  
  public static class MigrationContainer
  {
    private SparseArrayCompat<SparseArrayCompat<Migration>> mMigrations = new SparseArrayCompat();
    
    public MigrationContainer() {}
    
    private void addMigration(Migration paramMigration)
    {
      int i = startVersion;
      int j = endVersion;
      Object localObject2 = (SparseArrayCompat)mMigrations.get(i);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new SparseArrayCompat();
        mMigrations.put(i, localObject1);
      }
      localObject2 = (Migration)((SparseArrayCompat)localObject1).get(j);
      if (localObject2 != null)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Overriding migration ");
        localStringBuilder.append(localObject2);
        localStringBuilder.append(" with ");
        localStringBuilder.append(paramMigration);
        Log.w("ROOM", localStringBuilder.toString());
      }
      ((SparseArrayCompat)localObject1).append(j, paramMigration);
    }
    
    private List findUpMigrationPath(List paramList, boolean paramBoolean, int paramInt1, int paramInt2)
    {
      int i;
      int j;
      if (paramBoolean)
      {
        i = -1;
        j = paramInt1;
      }
      else
      {
        i = 1;
        j = paramInt1;
      }
      while (paramBoolean ? j < paramInt2 : j > paramInt2)
      {
        SparseArrayCompat localSparseArrayCompat = (SparseArrayCompat)mMigrations.get(j);
        if (localSparseArrayCompat == null) {
          return null;
        }
        int k = localSparseArrayCompat.size();
        int i1 = 0;
        if (paramBoolean)
        {
          paramInt1 = k - 1;
          k = -1;
        }
        else
        {
          paramInt1 = 0;
        }
        int m;
        int n;
        for (;;)
        {
          m = i1;
          n = j;
          if (paramInt1 == k) {
            break;
          }
          n = localSparseArrayCompat.keyAt(paramInt1);
          if (paramBoolean) {
            if ((n > paramInt2) || (n <= j)) {}
          }
          for (;;)
          {
            m = 1;
            break;
            do
            {
              m = 0;
              break;
            } while ((n < paramInt2) || (n >= j));
          }
          if (m != 0)
          {
            paramList.add(localSparseArrayCompat.valueAt(paramInt1));
            m = 1;
            break;
          }
          paramInt1 += i;
        }
        j = n;
        if (m == 0) {
          return null;
        }
      }
      return paramList;
    }
    
    public void addMigrations(Migration... paramVarArgs)
    {
      int j = paramVarArgs.length;
      int i = 0;
      while (i < j)
      {
        addMigration(paramVarArgs[i]);
        i += 1;
      }
    }
    
    public List findMigrationPath(int paramInt1, int paramInt2)
    {
      if (paramInt1 == paramInt2) {
        return Collections.emptyList();
      }
      boolean bool;
      if (paramInt2 > paramInt1) {
        bool = true;
      } else {
        bool = false;
      }
      return findUpMigrationPath(new ArrayList(), bool, paramInt1, paramInt2);
    }
  }
}

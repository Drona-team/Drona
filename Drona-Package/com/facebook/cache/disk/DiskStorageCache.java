package com.facebook.cache.disk;

import android.content.Context;
import com.facebook.binaryresource.BinaryResource;
import com.facebook.cache.common.CacheErrorLogger;
import com.facebook.cache.common.CacheErrorLogger.CacheErrorCategory;
import com.facebook.cache.common.CacheEvent;
import com.facebook.cache.common.CacheEventListener;
import com.facebook.cache.common.CacheEventListener.EvictionReason;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.CacheKeyUtil;
import com.facebook.cache.common.WriterCallback;
import com.facebook.common.disk.DiskTrimmable;
import com.facebook.common.disk.DiskTrimmableRegistry;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.logging.FLog;
import com.facebook.common.statfs.StatFsHelper;
import com.facebook.common.statfs.StatFsHelper.StorageType;
import com.facebook.common.time.Clock;
import com.facebook.common.time.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class DiskStorageCache
  implements FileCache, DiskTrimmable
{
  private static final long FILECACHE_SIZE_UPDATE_PERIOD_MS = TimeUnit.MINUTES.toMillis(30L);
  private static final long FUTURE_TIMESTAMP_THRESHOLD_MS;
  private static final String SHARED_PREFS_FILENAME_PREFIX = "disk_entries_list";
  public static final int START_OF_VERSIONING = 1;
  private static final double TRIMMING_LOWER_BOUND = 0.02D;
  private static final long UNINITIALIZED = -1L;
  private static final Class<?> internal = DiskStorageCache.class;
  private final CacheErrorLogger mCacheErrorLogger;
  private final CacheEventListener mCacheEventListener;
  private long mCacheSizeLastUpdateTime;
  private long mCacheSizeLimit;
  private final long mCacheSizeLimitMinimum;
  private final CacheStats mCacheStats;
  private final Clock mClock;
  private final CountDownLatch mCountDownLatch;
  private final long mDefaultCacheSizeLimit;
  private final EntryEvictionComparatorSupplier mEntryEvictionComparatorSupplier;
  private final boolean mIndexPopulateAtStartupEnabled;
  private boolean mIndexReady;
  private final Object mLock = new Object();
  private final long mLowDiskSpaceCacheSizeLimit;
  @VisibleForTesting
  @GuardedBy("mLock")
  final Set<String> mResourceIndex;
  private final StatFsHelper mStatFsHelper;
  private final DiskStorage mStorage;
  
  static
  {
    FUTURE_TIMESTAMP_THRESHOLD_MS = TimeUnit.HOURS.toMillis(2L);
  }
  
  public DiskStorageCache(DiskStorage paramDiskStorage, EntryEvictionComparatorSupplier paramEntryEvictionComparatorSupplier, Params paramParams, CacheEventListener paramCacheEventListener, CacheErrorLogger paramCacheErrorLogger, DiskTrimmableRegistry paramDiskTrimmableRegistry, Context paramContext, Executor paramExecutor, boolean paramBoolean)
  {
    mLowDiskSpaceCacheSizeLimit = mLowDiskSpaceCacheSizeLimit;
    mDefaultCacheSizeLimit = mDefaultCacheSizeLimit;
    mCacheSizeLimit = mDefaultCacheSizeLimit;
    mStatFsHelper = StatFsHelper.getInstance();
    mStorage = paramDiskStorage;
    mEntryEvictionComparatorSupplier = paramEntryEvictionComparatorSupplier;
    mCacheSizeLastUpdateTime = -1L;
    mCacheEventListener = paramCacheEventListener;
    mCacheSizeLimitMinimum = mCacheSizeLimitMinimum;
    mCacheErrorLogger = paramCacheErrorLogger;
    mCacheStats = new CacheStats();
    mClock = SystemClock.get();
    mIndexPopulateAtStartupEnabled = paramBoolean;
    mResourceIndex = new HashSet();
    if (paramDiskTrimmableRegistry != null) {
      paramDiskTrimmableRegistry.registerDiskTrimmable(this);
    }
    if (mIndexPopulateAtStartupEnabled)
    {
      mCountDownLatch = new CountDownLatch(1);
      paramExecutor.execute(new Runnable()
      {
        public void run()
        {
          Object localObject = mLock;
          try
          {
            DiskStorageCache.this.maybeUpdateFileCacheSize();
            DiskStorageCache.access$202(DiskStorageCache.this, true);
            mCountDownLatch.countDown();
            return;
          }
          catch (Throwable localThrowable)
          {
            throw localThrowable;
          }
        }
      });
      return;
    }
    mCountDownLatch = new CountDownLatch(0);
  }
  
  private BinaryResource endInsert(DiskStorage.Inserter paramInserter, CacheKey paramCacheKey, String paramString)
    throws IOException
  {
    Object localObject = mLock;
    try
    {
      paramInserter = paramInserter.commit(paramCacheKey);
      mResourceIndex.add(paramString);
      mCacheStats.increment(paramInserter.size(), 1L);
      return paramInserter;
    }
    catch (Throwable paramInserter)
    {
      throw paramInserter;
    }
  }
  
  private void evictAboveSize(long paramLong, CacheEventListener.EvictionReason paramEvictionReason)
    throws IOException
  {
    Object localObject1 = mStorage;
    try
    {
      localObject1 = getSortedEntries(((DiskStorage)localObject1).getEntries());
      long l2 = mCacheStats.getSize();
      int i = 0;
      localObject1 = ((Collection)localObject1).iterator();
      long l1 = 0L;
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (DiskStorage.Entry)((Iterator)localObject1).next();
        if (l1 > l2 - paramLong) {
          break;
        }
        long l3 = mStorage.remove((DiskStorage.Entry)localObject2);
        mResourceIndex.remove(((DiskStorage.Entry)localObject2).getId());
        if (l3 > 0L)
        {
          i += 1;
          l1 += l3;
          localObject2 = SettableCacheEvent.obtain().setResourceId(((DiskStorage.Entry)localObject2).getId()).setEvictionReason(paramEvictionReason).setItemSize(l3).setCacheSize(l2 - l1).setCacheLimit(paramLong);
          mCacheEventListener.onEviction((CacheEvent)localObject2);
          ((SettableCacheEvent)localObject2).recycle();
        }
      }
      mCacheStats.increment(-l1, -i);
      mStorage.purgeUnexpectedResources();
      return;
    }
    catch (IOException paramEvictionReason)
    {
      localObject1 = mCacheErrorLogger;
      Object localObject2 = CacheErrorLogger.CacheErrorCategory.EVICTION;
      Class localClass = internal;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("evictAboveSize: ");
      localStringBuilder.append(paramEvictionReason.getMessage());
      ((CacheErrorLogger)localObject1).logError((CacheErrorLogger.CacheErrorCategory)localObject2, localClass, localStringBuilder.toString(), paramEvictionReason);
      throw paramEvictionReason;
    }
  }
  
  private Collection getSortedEntries(Collection paramCollection)
  {
    long l1 = mClock.now();
    long l2 = FUTURE_TIMESTAMP_THRESHOLD_MS;
    ArrayList localArrayList1 = new ArrayList(paramCollection.size());
    ArrayList localArrayList2 = new ArrayList(paramCollection.size());
    paramCollection = paramCollection.iterator();
    while (paramCollection.hasNext())
    {
      DiskStorage.Entry localEntry = (DiskStorage.Entry)paramCollection.next();
      if (localEntry.getTimestamp() > l1 + l2) {
        localArrayList1.add(localEntry);
      } else {
        localArrayList2.add(localEntry);
      }
    }
    Collections.sort(localArrayList2, mEntryEvictionComparatorSupplier.getLastSms());
    localArrayList1.addAll(localArrayList2);
    return localArrayList1;
  }
  
  private void maybeEvictFilesInCacheDir()
    throws IOException
  {
    Object localObject = mLock;
    try
    {
      boolean bool = maybeUpdateFileCacheSize();
      updateFileCacheSizeLimit();
      long l = mCacheStats.getSize();
      if ((l > mCacheSizeLimit) && (!bool))
      {
        mCacheStats.reset();
        maybeUpdateFileCacheSize();
      }
      if (l > mCacheSizeLimit) {
        evictAboveSize(mCacheSizeLimit * 9L / 10L, CacheEventListener.EvictionReason.CACHE_FULL);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private boolean maybeUpdateFileCacheSize()
  {
    long l = mClock.now();
    if ((mCacheStats.isInitialized()) && (mCacheSizeLastUpdateTime != -1L) && (l - mCacheSizeLastUpdateTime <= FILECACHE_SIZE_UPDATE_PERIOD_MS)) {
      return false;
    }
    return maybeUpdateFileCacheSizeAndIndex();
  }
  
  private boolean maybeUpdateFileCacheSizeAndIndex()
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a5 = a4\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  private DiskStorage.Inserter startInsert(String paramString, CacheKey paramCacheKey)
    throws IOException
  {
    maybeEvictFilesInCacheDir();
    return mStorage.insert(paramString, paramCacheKey);
  }
  
  /* Error */
  private void trimBy(double paramDouble)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 101	com/facebook/cache/disk/DiskStorageCache:mLock	Ljava/lang/Object;
    //   4: astore 7
    //   6: aload 7
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 133	com/facebook/cache/disk/DiskStorageCache:mCacheStats	Lcom/facebook/cache/disk/DiskStorageCache$CacheStats;
    //   13: astore 8
    //   15: aload 8
    //   17: invokevirtual 356	com/facebook/cache/disk/DiskStorageCache$CacheStats:reset	()V
    //   20: aload_0
    //   21: invokespecial 178	com/facebook/cache/disk/DiskStorageCache:maybeUpdateFileCacheSize	()Z
    //   24: pop
    //   25: aload_0
    //   26: getfield 133	com/facebook/cache/disk/DiskStorageCache:mCacheStats	Lcom/facebook/cache/disk/DiskStorageCache$CacheStats;
    //   29: astore 8
    //   31: aload 8
    //   33: invokevirtual 228	com/facebook/cache/disk/DiskStorageCache$CacheStats:getSize	()J
    //   36: lstore_3
    //   37: dload_1
    //   38: lload_3
    //   39: l2d
    //   40: dmul
    //   41: d2l
    //   42: lstore 5
    //   44: getstatic 396	com/facebook/cache/common/CacheEventListener$EvictionReason:CACHE_MANAGER_TRIMMED	Lcom/facebook/cache/common/CacheEventListener$EvictionReason;
    //   47: astore 8
    //   49: aload_0
    //   50: lload_3
    //   51: lload 5
    //   53: lsub
    //   54: aload 8
    //   56: invokespecial 368	com/facebook/cache/disk/DiskStorageCache:evictAboveSize	(JLcom/facebook/cache/common/CacheEventListener$EvictionReason;)V
    //   59: goto +73 -> 132
    //   62: astore 8
    //   64: goto +72 -> 136
    //   67: astore 8
    //   69: aload_0
    //   70: getfield 130	com/facebook/cache/disk/DiskStorageCache:mCacheErrorLogger	Lcom/facebook/cache/common/CacheErrorLogger;
    //   73: astore 9
    //   75: getstatic 297	com/facebook/cache/common/CacheErrorLogger$CacheErrorCategory:EVICTION	Lcom/facebook/cache/common/CacheErrorLogger$CacheErrorCategory;
    //   78: astore 10
    //   80: getstatic 73	com/facebook/cache/disk/DiskStorageCache:internal	Ljava/lang/Class;
    //   83: astore 11
    //   85: new 299	java/lang/StringBuilder
    //   88: dup
    //   89: invokespecial 300	java/lang/StringBuilder:<init>	()V
    //   92: astore 12
    //   94: aload 12
    //   96: ldc_w 398
    //   99: invokevirtual 306	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: pop
    //   103: aload 12
    //   105: aload 8
    //   107: invokevirtual 309	java/io/IOException:getMessage	()Ljava/lang/String;
    //   110: invokevirtual 306	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   113: pop
    //   114: aload 9
    //   116: aload 10
    //   118: aload 11
    //   120: aload 12
    //   122: invokevirtual 312	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   125: aload 8
    //   127: invokeinterface 318 5 0
    //   132: aload 7
    //   134: monitorexit
    //   135: return
    //   136: aload 7
    //   138: monitorexit
    //   139: aload 8
    //   141: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	142	0	this	DiskStorageCache
    //   0	142	1	paramDouble	double
    //   36	15	3	l1	long
    //   42	10	5	l2	long
    //   4	133	7	localObject1	Object
    //   13	42	8	localObject2	Object
    //   62	1	8	localThrowable	Throwable
    //   67	73	8	localIOException	IOException
    //   73	42	9	localCacheErrorLogger	CacheErrorLogger
    //   78	39	10	localCacheErrorCategory	CacheErrorLogger.CacheErrorCategory
    //   83	36	11	localClass	Class
    //   92	29	12	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   9	15	62	java/lang/Throwable
    //   15	25	62	java/lang/Throwable
    //   25	31	62	java/lang/Throwable
    //   31	37	62	java/lang/Throwable
    //   49	59	62	java/lang/Throwable
    //   69	132	62	java/lang/Throwable
    //   132	135	62	java/lang/Throwable
    //   136	139	62	java/lang/Throwable
    //   15	25	67	java/io/IOException
    //   31	37	67	java/io/IOException
    //   49	59	67	java/io/IOException
  }
  
  private void updateFileCacheSizeLimit()
  {
    StatFsHelper.StorageType localStorageType;
    if (mStorage.isExternal()) {
      localStorageType = StatFsHelper.StorageType.EXTERNAL;
    } else {
      localStorageType = StatFsHelper.StorageType.INTERNAL;
    }
    if (mStatFsHelper.testLowDiskSpace(localStorageType, mDefaultCacheSizeLimit - mCacheStats.getSize()))
    {
      mCacheSizeLimit = mLowDiskSpaceCacheSizeLimit;
      return;
    }
    mCacheSizeLimit = mDefaultCacheSizeLimit;
  }
  
  protected void awaitIndex()
  {
    CountDownLatch localCountDownLatch = mCountDownLatch;
    try
    {
      localCountDownLatch.await();
      return;
    }
    catch (InterruptedException localInterruptedException)
    {
      for (;;) {}
    }
    FLog.e(internal, "Memory Index is not ready yet. ");
  }
  
  public void clearAll()
  {
    Object localObject1 = mLock;
    try
    {
      Object localObject2 = mStorage;
      ((DiskStorage)localObject2).clearAll();
      localObject2 = mResourceIndex;
      ((Set)localObject2).clear();
      localObject2 = mCacheEventListener;
      ((CacheEventListener)localObject2).onCleared();
    }
    catch (Throwable localThrowable)
    {
      break label117;
    }
    catch (NullPointerException|IOException localNullPointerException)
    {
      CacheErrorLogger localCacheErrorLogger = mCacheErrorLogger;
      CacheErrorLogger.CacheErrorCategory localCacheErrorCategory = CacheErrorLogger.CacheErrorCategory.EVICTION;
      Class localClass = internal;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("clearAll: ");
      localStringBuilder.append(localNullPointerException.getMessage());
      localCacheErrorLogger.logError(localCacheErrorCategory, localClass, localStringBuilder.toString(), localNullPointerException);
    }
    mCacheStats.reset();
    return;
    label117:
    throw localNullPointerException;
  }
  
  /* Error */
  public long clearOldEntries(long paramLong)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 101	com/facebook/cache/disk/DiskStorageCache:mLock	Ljava/lang/Object;
    //   4: astore 21
    //   6: aload 21
    //   8: monitorenter
    //   9: aload_0
    //   10: getfield 141	com/facebook/cache/disk/DiskStorageCache:mClock	Lcom/facebook/common/time/Clock;
    //   13: astore 20
    //   15: aload 20
    //   17: invokeinterface 323 1 0
    //   22: lstore 13
    //   24: aload_0
    //   25: getfield 119	com/facebook/cache/disk/DiskStorageCache:mStorage	Lcom/facebook/cache/disk/DiskStorage;
    //   28: astore 20
    //   30: aload 20
    //   32: invokeinterface 221 1 0
    //   37: astore 20
    //   39: aload_0
    //   40: getfield 133	com/facebook/cache/disk/DiskStorageCache:mCacheStats	Lcom/facebook/cache/disk/DiskStorageCache$CacheStats;
    //   43: astore 22
    //   45: aload 22
    //   47: invokevirtual 228	com/facebook/cache/disk/DiskStorageCache$CacheStats:getSize	()J
    //   50: lstore 15
    //   52: iconst_0
    //   53: istore_3
    //   54: aload 20
    //   56: invokeinterface 234 1 0
    //   61: astore 20
    //   63: lconst_0
    //   64: lstore 7
    //   66: lconst_0
    //   67: lstore 5
    //   69: aload 20
    //   71: invokeinterface 239 1 0
    //   76: istore 19
    //   78: iload 19
    //   80: ifeq +220 -> 300
    //   83: aload 20
    //   85: invokeinterface 243 1 0
    //   90: astore 22
    //   92: aload 22
    //   94: checkcast 245	com/facebook/cache/disk/DiskStorage$Entry
    //   97: astore 22
    //   99: aload 22
    //   101: invokeinterface 332 1 0
    //   106: lstore 9
    //   108: lconst_1
    //   109: lload 13
    //   111: lload 9
    //   113: lsub
    //   114: invokestatic 450	java/lang/Math:abs	(J)J
    //   117: invokestatic 454	java/lang/Math:max	(JJ)J
    //   120: lstore 9
    //   122: lload 9
    //   124: lload_1
    //   125: lcmp
    //   126: iflt +144 -> 270
    //   129: aload_0
    //   130: getfield 119	com/facebook/cache/disk/DiskStorageCache:mStorage	Lcom/facebook/cache/disk/DiskStorage;
    //   133: astore 23
    //   135: aload 23
    //   137: aload 22
    //   139: invokeinterface 249 2 0
    //   144: lstore 17
    //   146: aload_0
    //   147: getfield 148	com/facebook/cache/disk/DiskStorageCache:mResourceIndex	Ljava/util/Set;
    //   150: astore 23
    //   152: aload 23
    //   154: aload 22
    //   156: invokeinterface 253 1 0
    //   161: invokeinterface 255 2 0
    //   166: pop
    //   167: lload 5
    //   169: lstore 9
    //   171: iload_3
    //   172: istore 4
    //   174: lload 7
    //   176: lstore 11
    //   178: lload 17
    //   180: lconst_0
    //   181: lcmp
    //   182: ifle +104 -> 286
    //   185: iload_3
    //   186: iconst_1
    //   187: iadd
    //   188: istore 4
    //   190: lload 7
    //   192: lload 17
    //   194: ladd
    //   195: lstore 11
    //   197: invokestatic 261	com/facebook/cache/disk/SettableCacheEvent:obtain	()Lcom/facebook/cache/disk/SettableCacheEvent;
    //   200: aload 22
    //   202: invokeinterface 253 1 0
    //   207: invokevirtual 265	com/facebook/cache/disk/SettableCacheEvent:setResourceId	(Ljava/lang/String;)Lcom/facebook/cache/disk/SettableCacheEvent;
    //   210: astore 22
    //   212: getstatic 457	com/facebook/cache/common/CacheEventListener$EvictionReason:CONTENT_STALE	Lcom/facebook/cache/common/CacheEventListener$EvictionReason;
    //   215: astore 23
    //   217: aload 22
    //   219: aload 23
    //   221: invokevirtual 269	com/facebook/cache/disk/SettableCacheEvent:setEvictionReason	(Lcom/facebook/cache/common/CacheEventListener$EvictionReason;)Lcom/facebook/cache/disk/SettableCacheEvent;
    //   224: lload 17
    //   226: invokevirtual 273	com/facebook/cache/disk/SettableCacheEvent:setItemSize	(J)Lcom/facebook/cache/disk/SettableCacheEvent;
    //   229: astore 22
    //   231: aload 22
    //   233: lload 15
    //   235: lload 11
    //   237: lsub
    //   238: invokevirtual 276	com/facebook/cache/disk/SettableCacheEvent:setCacheSize	(J)Lcom/facebook/cache/disk/SettableCacheEvent;
    //   241: astore 22
    //   243: aload_0
    //   244: getfield 125	com/facebook/cache/disk/DiskStorageCache:mCacheEventListener	Lcom/facebook/cache/common/CacheEventListener;
    //   247: astore 23
    //   249: aload 23
    //   251: aload 22
    //   253: invokeinterface 285 2 0
    //   258: aload 22
    //   260: invokevirtual 288	com/facebook/cache/disk/SettableCacheEvent:recycle	()V
    //   263: lload 5
    //   265: lstore 9
    //   267: goto +19 -> 286
    //   270: lload 5
    //   272: lload 9
    //   274: invokestatic 454	java/lang/Math:max	(JJ)J
    //   277: lstore 9
    //   279: lload 7
    //   281: lstore 11
    //   283: iload_3
    //   284: istore 4
    //   286: lload 9
    //   288: lstore 5
    //   290: iload 4
    //   292: istore_3
    //   293: lload 11
    //   295: lstore 7
    //   297: goto -228 -> 69
    //   300: aload_0
    //   301: getfield 119	com/facebook/cache/disk/DiskStorageCache:mStorage	Lcom/facebook/cache/disk/DiskStorage;
    //   304: astore 20
    //   306: aload 20
    //   308: invokeinterface 291 1 0
    //   313: lload 5
    //   315: lstore_1
    //   316: iload_3
    //   317: ifle +118 -> 435
    //   320: aload_0
    //   321: invokespecial 178	com/facebook/cache/disk/DiskStorageCache:maybeUpdateFileCacheSize	()Z
    //   324: pop
    //   325: aload_0
    //   326: getfield 133	com/facebook/cache/disk/DiskStorageCache:mCacheStats	Lcom/facebook/cache/disk/DiskStorageCache$CacheStats;
    //   329: astore 20
    //   331: lload 7
    //   333: lneg
    //   334: lstore_1
    //   335: iload_3
    //   336: ineg
    //   337: i2l
    //   338: lstore 7
    //   340: aload 20
    //   342: lload_1
    //   343: lload 7
    //   345: invokevirtual 212	com/facebook/cache/disk/DiskStorageCache$CacheStats:increment	(JJ)V
    //   348: lload 5
    //   350: lstore_1
    //   351: goto +84 -> 435
    //   354: astore 20
    //   356: goto +13 -> 369
    //   359: astore 20
    //   361: goto +79 -> 440
    //   364: astore 20
    //   366: lconst_0
    //   367: lstore 5
    //   369: aload_0
    //   370: getfield 130	com/facebook/cache/disk/DiskStorageCache:mCacheErrorLogger	Lcom/facebook/cache/common/CacheErrorLogger;
    //   373: astore 22
    //   375: getstatic 297	com/facebook/cache/common/CacheErrorLogger$CacheErrorCategory:EVICTION	Lcom/facebook/cache/common/CacheErrorLogger$CacheErrorCategory;
    //   378: astore 23
    //   380: getstatic 73	com/facebook/cache/disk/DiskStorageCache:internal	Ljava/lang/Class;
    //   383: astore 24
    //   385: new 299	java/lang/StringBuilder
    //   388: dup
    //   389: invokespecial 300	java/lang/StringBuilder:<init>	()V
    //   392: astore 25
    //   394: aload 25
    //   396: ldc_w 459
    //   399: invokevirtual 306	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   402: pop
    //   403: aload 25
    //   405: aload 20
    //   407: invokevirtual 309	java/io/IOException:getMessage	()Ljava/lang/String;
    //   410: invokevirtual 306	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   413: pop
    //   414: aload 22
    //   416: aload 23
    //   418: aload 24
    //   420: aload 25
    //   422: invokevirtual 312	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   425: aload 20
    //   427: invokeinterface 318 5 0
    //   432: lload 5
    //   434: lstore_1
    //   435: aload 21
    //   437: monitorexit
    //   438: lload_1
    //   439: lreturn
    //   440: aload 21
    //   442: monitorexit
    //   443: aload 20
    //   445: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	446	0	this	DiskStorageCache
    //   0	446	1	paramLong	long
    //   53	283	3	i	int
    //   172	119	4	j	int
    //   67	366	5	l1	long
    //   64	280	7	l2	long
    //   106	181	9	l3	long
    //   176	118	11	l4	long
    //   22	88	13	l5	long
    //   50	184	15	l6	long
    //   144	81	17	l7	long
    //   76	3	19	bool	boolean
    //   13	328	20	localObject1	Object
    //   354	1	20	localIOException1	IOException
    //   359	1	20	localThrowable	Throwable
    //   364	80	20	localIOException2	IOException
    //   4	437	21	localObject2	Object
    //   43	372	22	localObject3	Object
    //   133	284	23	localObject4	Object
    //   383	36	24	localClass	Class
    //   392	29	25	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   69	78	354	java/io/IOException
    //   83	92	354	java/io/IOException
    //   99	108	354	java/io/IOException
    //   108	122	354	java/io/IOException
    //   135	146	354	java/io/IOException
    //   152	167	354	java/io/IOException
    //   197	212	354	java/io/IOException
    //   217	231	354	java/io/IOException
    //   231	243	354	java/io/IOException
    //   249	263	354	java/io/IOException
    //   270	279	354	java/io/IOException
    //   306	313	354	java/io/IOException
    //   320	325	354	java/io/IOException
    //   340	348	354	java/io/IOException
    //   9	15	359	java/lang/Throwable
    //   15	24	359	java/lang/Throwable
    //   24	30	359	java/lang/Throwable
    //   30	39	359	java/lang/Throwable
    //   39	45	359	java/lang/Throwable
    //   45	52	359	java/lang/Throwable
    //   54	63	359	java/lang/Throwable
    //   69	78	359	java/lang/Throwable
    //   83	92	359	java/lang/Throwable
    //   92	99	359	java/lang/Throwable
    //   99	108	359	java/lang/Throwable
    //   108	122	359	java/lang/Throwable
    //   129	135	359	java/lang/Throwable
    //   135	146	359	java/lang/Throwable
    //   146	152	359	java/lang/Throwable
    //   152	167	359	java/lang/Throwable
    //   197	212	359	java/lang/Throwable
    //   212	217	359	java/lang/Throwable
    //   217	231	359	java/lang/Throwable
    //   231	243	359	java/lang/Throwable
    //   243	249	359	java/lang/Throwable
    //   249	263	359	java/lang/Throwable
    //   270	279	359	java/lang/Throwable
    //   300	306	359	java/lang/Throwable
    //   306	313	359	java/lang/Throwable
    //   320	325	359	java/lang/Throwable
    //   340	348	359	java/lang/Throwable
    //   369	432	359	java/lang/Throwable
    //   435	438	359	java/lang/Throwable
    //   440	443	359	java/lang/Throwable
    //   15	24	364	java/io/IOException
    //   30	39	364	java/io/IOException
    //   45	52	364	java/io/IOException
    //   54	63	364	java/io/IOException
  }
  
  public long getCount()
  {
    return mCacheStats.getCount();
  }
  
  public DiskStorage.DiskDumpInfo getDumpInfo()
    throws IOException
  {
    return mStorage.getDumpInfo();
  }
  
  public BinaryResource getResource(CacheKey paramCacheKey)
  {
    SettableCacheEvent localSettableCacheEvent = SettableCacheEvent.obtain().setCacheKey(paramCacheKey);
    label216:
    for (;;)
    {
      try
      {
        Object localObject2 = mLock;
        int i;
        try
        {
          List localList = CacheKeyUtil.getResourceIds(paramCacheKey);
          i = 0;
          String str = null;
          Object localObject1 = null;
          if (i < localList.size())
          {
            str = (String)localList.get(i);
            localSettableCacheEvent.setResourceId(str);
            BinaryResource localBinaryResource = mStorage.getResource(str, paramCacheKey);
            localObject1 = localBinaryResource;
            if (localBinaryResource == null) {
              break label216;
            }
          }
          if (localObject1 == null)
          {
            mCacheEventListener.onMiss(localSettableCacheEvent);
            mResourceIndex.remove(str);
          }
          else
          {
            mCacheEventListener.onHit(localSettableCacheEvent);
            mResourceIndex.add(str);
          }
          localSettableCacheEvent.recycle();
          return localObject1;
        }
        catch (Throwable paramCacheKey) {}
        i += 1;
      }
      catch (Throwable paramCacheKey)
      {
        try
        {
          throw paramCacheKey;
        }
        catch (IOException paramCacheKey)
        {
          mCacheErrorLogger.logError(CacheErrorLogger.CacheErrorCategory.GENERIC_IO, internal, "getResource", paramCacheKey);
          localSettableCacheEvent.setException((IOException)paramCacheKey);
          mCacheEventListener.onReadException(localSettableCacheEvent);
          localSettableCacheEvent.recycle();
          return null;
        }
        paramCacheKey = paramCacheKey;
        localSettableCacheEvent.recycle();
        throw paramCacheKey;
      }
    }
  }
  
  public long getSize()
  {
    return mCacheStats.getSize();
  }
  
  public boolean hasKey(CacheKey paramCacheKey)
  {
    localObject1 = mLock;
    for (;;)
    {
      try
      {
        if (hasKeySync(paramCacheKey)) {
          return true;
        }
      }
      catch (Throwable paramCacheKey)
      {
        List localList;
        int i;
        int j;
        throw paramCacheKey;
      }
      try
      {
        localList = CacheKeyUtil.getResourceIds(paramCacheKey);
        i = 0;
        j = localList.size();
        if (i < j)
        {
          Object localObject2 = localList.get(i);
          localObject2 = (String)localObject2;
          DiskStorage localDiskStorage = mStorage;
          boolean bool = localDiskStorage.contains((String)localObject2, paramCacheKey);
          if (bool)
          {
            paramCacheKey = mResourceIndex;
            paramCacheKey.add(localObject2);
            return true;
          }
          i += 1;
        }
        else
        {
          return false;
        }
      }
      catch (IOException paramCacheKey) {}
    }
    return false;
  }
  
  public boolean hasKeySync(CacheKey paramCacheKey)
  {
    Object localObject = mLock;
    try
    {
      paramCacheKey = CacheKeyUtil.getResourceIds(paramCacheKey);
      int i = 0;
      while (i < paramCacheKey.size())
      {
        String str = (String)paramCacheKey.get(i);
        if (mResourceIndex.contains(str)) {
          return true;
        }
        i += 1;
      }
      return false;
    }
    catch (Throwable paramCacheKey)
    {
      throw paramCacheKey;
    }
  }
  
  public BinaryResource insert(CacheKey paramCacheKey, WriterCallback paramWriterCallback)
    throws IOException
  {
    SettableCacheEvent localSettableCacheEvent = SettableCacheEvent.obtain().setCacheKey(paramCacheKey);
    mCacheEventListener.onWriteAttempt(localSettableCacheEvent);
    Object localObject = mLock;
    try
    {
      String str = CacheKeyUtil.getFirstResourceId(paramCacheKey);
      localSettableCacheEvent.setResourceId(str);
      try
      {
        localObject = startInsert(str, paramCacheKey);
        try
        {
          ((DiskStorage.Inserter)localObject).writeData(paramWriterCallback, paramCacheKey);
          paramCacheKey = endInsert((DiskStorage.Inserter)localObject, paramCacheKey, str);
          localSettableCacheEvent.setItemSize(paramCacheKey.size()).setCacheSize(mCacheStats.getSize());
          mCacheEventListener.onWriteSuccess(localSettableCacheEvent);
          bool = ((DiskStorage.Inserter)localObject).cleanUp();
          if (!bool)
          {
            paramWriterCallback = internal;
            FLog.e(paramWriterCallback, "Failed to delete temp file");
          }
          localSettableCacheEvent.recycle();
          return paramCacheKey;
        }
        catch (Throwable paramCacheKey)
        {
          boolean bool = ((DiskStorage.Inserter)localObject).cleanUp();
          if (!bool)
          {
            paramWriterCallback = internal;
            FLog.e(paramWriterCallback, "Failed to delete temp file");
          }
          throw paramCacheKey;
        }
        localSettableCacheEvent.recycle();
      }
      catch (Throwable paramCacheKey) {}catch (IOException paramCacheKey)
      {
        localSettableCacheEvent.setException((IOException)paramCacheKey);
        mCacheEventListener.onWriteException(localSettableCacheEvent);
        FLog.e(internal, "Failed inserting a file into the cache", paramCacheKey);
        throw paramCacheKey;
      }
      throw paramCacheKey;
    }
    catch (Throwable paramCacheKey)
    {
      throw paramCacheKey;
    }
  }
  
  public boolean isEnabled()
  {
    return mStorage.isEnabled();
  }
  
  public boolean isIndexReady()
  {
    return (mIndexReady) || (!mIndexPopulateAtStartupEnabled);
  }
  
  public boolean probe(CacheKey paramCacheKey)
  {
    Object localObject3 = mLock;
    Object localObject1;
    for (;;)
    {
      Object localObject2;
      try
      {
        List localList = CacheKeyUtil.getResourceIds(paramCacheKey);
        localObject1 = null;
        int i = 0;
        String str;
        try
        {
          if (i < localList.size())
          {
            str = (String)localList.get(i);
            localObject2 = str;
          }
        }
        catch (Throwable localThrowable1) {}
      }
      catch (Throwable localThrowable2)
      {
        localObject1 = null;
      }
      try
      {
        if (mStorage.touch(str, paramCacheKey))
        {
          localObject2 = str;
          mResourceIndex.add(str);
          localObject2 = str;
          return true;
        }
        i += 1;
        localObject1 = str;
      }
      catch (Throwable localThrowable3)
      {
        for (;;)
        {
          localObject1 = localObject2;
        }
      }
    }
    return false;
    localObject2 = localObject1;
    try
    {
      throw localThrowable2;
    }
    catch (IOException localIOException) {}
    paramCacheKey = SettableCacheEvent.obtain().setCacheKey(paramCacheKey).setResourceId(localObject1).setException(localThrowable3);
    mCacheEventListener.onReadException(paramCacheKey);
    paramCacheKey.recycle();
    return false;
  }
  
  public void remove(CacheKey paramCacheKey)
  {
    Object localObject1 = mLock;
    try
    {
      paramCacheKey = CacheKeyUtil.getResourceIds(paramCacheKey);
      int i = 0;
      for (;;)
      {
        int j = paramCacheKey.size();
        if (i >= j) {
          break;
        }
        localObject2 = paramCacheKey.get(i);
        localObject2 = (String)localObject2;
        localObject3 = mStorage;
        ((DiskStorage)localObject3).remove((String)localObject2);
        localObject3 = mResourceIndex;
        ((Set)localObject3).remove(localObject2);
        i += 1;
      }
    }
    catch (Throwable paramCacheKey) {}catch (IOException paramCacheKey)
    {
      Object localObject2 = mCacheErrorLogger;
      Object localObject3 = CacheErrorLogger.CacheErrorCategory.DELETE_FILE;
      Class localClass = internal;
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("delete: ");
      localStringBuilder.append(paramCacheKey.getMessage());
      ((CacheErrorLogger)localObject2).logError((CacheErrorLogger.CacheErrorCategory)localObject3, localClass, localStringBuilder.toString(), paramCacheKey);
      return;
    }
    throw paramCacheKey;
  }
  
  public void trimToMinimum()
  {
    Object localObject = mLock;
    try
    {
      maybeUpdateFileCacheSize();
      long l = mCacheStats.getSize();
      if ((mCacheSizeLimitMinimum > 0L) && (l > 0L) && (l >= mCacheSizeLimitMinimum))
      {
        double d = 1.0D - mCacheSizeLimitMinimum / l;
        if (d > 0.02D) {
          trimBy(d);
        }
        return;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void trimToNothing()
  {
    clearAll();
  }
  
  @VisibleForTesting
  static class CacheStats
  {
    private long mCount = -1L;
    private boolean mInitialized = false;
    private long mSize = -1L;
    
    CacheStats() {}
    
    public long getCount()
    {
      try
      {
        long l = mCount;
        return l;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public long getSize()
    {
      try
      {
        long l = mSize;
        return l;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void increment(long paramLong1, long paramLong2)
    {
      try
      {
        if (mInitialized)
        {
          mSize += paramLong1;
          mCount += paramLong2;
        }
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void init(long paramLong1, long paramLong2)
    {
      try
      {
        mCount = paramLong2;
        mSize = paramLong1;
        mInitialized = true;
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public boolean isInitialized()
    {
      try
      {
        boolean bool = mInitialized;
        return bool;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
    
    public void reset()
    {
      try
      {
        mInitialized = false;
        mCount = -1L;
        mSize = -1L;
        return;
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
    }
  }
  
  public static class Params
  {
    public final long mCacheSizeLimitMinimum;
    public final long mDefaultCacheSizeLimit;
    public final long mLowDiskSpaceCacheSizeLimit;
    
    public Params(long paramLong1, long paramLong2, long paramLong3)
    {
      mCacheSizeLimitMinimum = paramLong1;
      mLowDiskSpaceCacheSizeLimit = paramLong2;
      mDefaultCacheSizeLimit = paramLong3;
    }
  }
}
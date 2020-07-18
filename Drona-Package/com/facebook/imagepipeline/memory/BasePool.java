package com.facebook.imagepipeline.memory;

import android.util.SparseArray;
import android.util.SparseIntArray;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Sets;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.memory.Pool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.NotThreadSafe;

public abstract class BasePool<V>
  implements Pool<V>
{
  private final Class<?> TAG = getClass();
  private boolean mAllowNewBuckets;
  @VisibleForTesting
  final SparseArray<Bucket<V>> mBuckets;
  @VisibleForTesting
  @GuardedBy("this")
  final Counter mFree;
  @VisibleForTesting
  final Set<V> mInUseValues;
  final MemoryTrimmableRegistry mMemoryTrimmableRegistry;
  final PoolParams mPoolParams;
  private final PoolStatsTracker mPoolStatsTracker;
  @VisibleForTesting
  @GuardedBy("this")
  final Counter mUsed;
  
  public BasePool(MemoryTrimmableRegistry paramMemoryTrimmableRegistry, PoolParams paramPoolParams, PoolStatsTracker paramPoolStatsTracker)
  {
    mMemoryTrimmableRegistry = ((MemoryTrimmableRegistry)Preconditions.checkNotNull(paramMemoryTrimmableRegistry));
    mPoolParams = ((PoolParams)Preconditions.checkNotNull(paramPoolParams));
    mPoolStatsTracker = ((PoolStatsTracker)Preconditions.checkNotNull(paramPoolStatsTracker));
    mBuckets = new SparseArray();
    if (mPoolParams.fixBucketsReinitialization) {
      initBuckets();
    } else {
      legacyInitBuckets(new SparseIntArray(0));
    }
    mInUseValues = Sets.newIdentityHashSet();
    mFree = new Counter();
    mUsed = new Counter();
  }
  
  private void ensurePoolSizeInvariant()
  {
    for (;;)
    {
      try
      {
        if (!isMaxSizeSoftCapExceeded()) {
          break label39;
        }
        if (mFree.mNumBytes != 0) {
          break label34;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      Preconditions.checkState(bool);
      return;
      label34:
      boolean bool = false;
      continue;
      label39:
      bool = true;
    }
  }
  
  private void fillBuckets(SparseIntArray paramSparseIntArray)
  {
    mBuckets.clear();
    int i = 0;
    while (i < paramSparseIntArray.size())
    {
      int j = paramSparseIntArray.keyAt(i);
      int k = paramSparseIntArray.valueAt(i);
      mBuckets.put(j, new Bucket(getSizeInBytes(j), k, 0, mPoolParams.fixBucketsReinitialization));
      i += 1;
    }
  }
  
  private Bucket getBucketIfPresent(int paramInt)
  {
    try
    {
      Bucket localBucket = (Bucket)mBuckets.get(paramInt);
      return localBucket;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void initBuckets()
  {
    try
    {
      SparseIntArray localSparseIntArray = mPoolParams.bucketSizes;
      if (localSparseIntArray != null)
      {
        fillBuckets(localSparseIntArray);
        mAllowNewBuckets = false;
      }
      else
      {
        mAllowNewBuckets = true;
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private void legacyInitBuckets(SparseIntArray paramSparseIntArray)
  {
    try
    {
      Preconditions.checkNotNull(paramSparseIntArray);
      mBuckets.clear();
      SparseIntArray localSparseIntArray = mPoolParams.bucketSizes;
      if (localSparseIntArray != null)
      {
        int i = 0;
        while (i < localSparseIntArray.size())
        {
          int j = localSparseIntArray.keyAt(i);
          int k = localSparseIntArray.valueAt(i);
          int m = paramSparseIntArray.get(j, 0);
          mBuckets.put(j, new Bucket(getSizeInBytes(j), k, m, mPoolParams.fixBucketsReinitialization));
          i += 1;
        }
        mAllowNewBuckets = false;
      }
      else
      {
        mAllowNewBuckets = true;
      }
      return;
    }
    catch (Throwable paramSparseIntArray)
    {
      throw paramSparseIntArray;
    }
  }
  
  private void logStats()
  {
    if (FLog.isLoggable(2)) {
      FLog.v(TAG, "Used = (%d, %d); Free = (%d, %d)", Integer.valueOf(mUsed.mCount), Integer.valueOf(mUsed.mNumBytes), Integer.valueOf(mFree.mCount), Integer.valueOf(mFree.mNumBytes));
    }
  }
  
  private List refillBuckets()
  {
    ArrayList localArrayList = new ArrayList(mBuckets.size());
    int j = mBuckets.size();
    int i = 0;
    while (i < j)
    {
      Bucket localBucket = (Bucket)mBuckets.valueAt(i);
      int k = mItemSize;
      int m = mMaxLength;
      int n = localBucket.getInUseCount();
      if (localBucket.getFreeListSize() > 0) {
        localArrayList.add(localBucket);
      }
      mBuckets.setValueAt(i, new Bucket(getSizeInBytes(k), m, n, mPoolParams.fixBucketsReinitialization));
      i += 1;
    }
    return localArrayList;
  }
  
  protected abstract Object alloc(int paramInt);
  
  boolean canAllocate(int paramInt)
  {
    try
    {
      int i = mPoolParams.maxSizeHardCap;
      if (paramInt > i - mUsed.mNumBytes)
      {
        mPoolStatsTracker.onHardCapReached();
        return false;
      }
      int j = mPoolParams.maxSizeSoftCap;
      if (paramInt > j - (mUsed.mNumBytes + mFree.mNumBytes)) {
        trimToSize(j - paramInt);
      }
      if (paramInt > i - (mUsed.mNumBytes + mFree.mNumBytes))
      {
        mPoolStatsTracker.onHardCapReached();
        return false;
      }
      return true;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected abstract void free(Object paramObject);
  
  /* Error */
  public Object get(int paramInt)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 237	com/facebook/imagepipeline/memory/BasePool:ensurePoolSizeInvariant	()V
    //   4: aload_0
    //   5: iload_1
    //   6: invokevirtual 240	com/facebook/imagepipeline/memory/BasePool:getBucketedSize	(I)I
    //   9: istore_1
    //   10: aload_0
    //   11: monitorenter
    //   12: aload_0
    //   13: iload_1
    //   14: invokevirtual 243	com/facebook/imagepipeline/memory/BasePool:getBucket	(I)Lcom/facebook/imagepipeline/memory/Bucket;
    //   17: astore_3
    //   18: aload_3
    //   19: ifnull +106 -> 125
    //   22: aload_0
    //   23: aload_3
    //   24: invokevirtual 247	com/facebook/imagepipeline/memory/BasePool:getValue	(Lcom/facebook/imagepipeline/memory/Bucket;)Ljava/lang/Object;
    //   27: astore 4
    //   29: aload 4
    //   31: ifnull +94 -> 125
    //   34: aload_0
    //   35: getfield 103	com/facebook/imagepipeline/memory/BasePool:mInUseValues	Ljava/util/Set;
    //   38: aload 4
    //   40: invokeinterface 250 2 0
    //   45: invokestatic 124	com/facebook/common/internal/Preconditions:checkState	(Z)V
    //   48: aload_0
    //   49: aload 4
    //   51: invokevirtual 254	com/facebook/imagepipeline/memory/BasePool:getBucketedSizeForValue	(Ljava/lang/Object;)I
    //   54: istore_1
    //   55: aload_0
    //   56: iload_1
    //   57: invokevirtual 144	com/facebook/imagepipeline/memory/BasePool:getSizeInBytes	(I)I
    //   60: istore_2
    //   61: aload_0
    //   62: getfield 108	com/facebook/imagepipeline/memory/BasePool:mUsed	Lcom/facebook/imagepipeline/memory/BasePool$Counter;
    //   65: iload_2
    //   66: invokevirtual 257	com/facebook/imagepipeline/memory/BasePool$Counter:increment	(I)V
    //   69: aload_0
    //   70: getfield 106	com/facebook/imagepipeline/memory/BasePool:mFree	Lcom/facebook/imagepipeline/memory/BasePool$Counter;
    //   73: iload_2
    //   74: invokevirtual 260	com/facebook/imagepipeline/memory/BasePool$Counter:decrement	(I)V
    //   77: aload_0
    //   78: getfield 75	com/facebook/imagepipeline/memory/BasePool:mPoolStatsTracker	Lcom/facebook/imagepipeline/memory/PoolStatsTracker;
    //   81: iload_2
    //   82: invokeinterface 263 2 0
    //   87: aload_0
    //   88: invokespecial 265	com/facebook/imagepipeline/memory/BasePool:logStats	()V
    //   91: iconst_2
    //   92: invokestatic 175	com/facebook/common/logging/FLog:isLoggable	(I)Z
    //   95: ifeq +25 -> 120
    //   98: aload_0
    //   99: getfield 57	com/facebook/imagepipeline/memory/BasePool:TAG	Ljava/lang/Class;
    //   102: ldc_w 267
    //   105: aload 4
    //   107: invokestatic 272	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   110: invokestatic 186	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   113: iload_1
    //   114: invokestatic 186	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   117: invokestatic 275	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   120: aload_0
    //   121: monitorexit
    //   122: aload 4
    //   124: areturn
    //   125: aload_0
    //   126: iload_1
    //   127: invokevirtual 144	com/facebook/imagepipeline/memory/BasePool:getSizeInBytes	(I)I
    //   130: istore_2
    //   131: aload_0
    //   132: iload_2
    //   133: invokevirtual 277	com/facebook/imagepipeline/memory/BasePool:canAllocate	(I)Z
    //   136: ifeq +147 -> 283
    //   139: aload_0
    //   140: getfield 108	com/facebook/imagepipeline/memory/BasePool:mUsed	Lcom/facebook/imagepipeline/memory/BasePool$Counter;
    //   143: iload_2
    //   144: invokevirtual 257	com/facebook/imagepipeline/memory/BasePool$Counter:increment	(I)V
    //   147: aload_3
    //   148: ifnull +7 -> 155
    //   151: aload_3
    //   152: invokevirtual 280	com/facebook/imagepipeline/memory/Bucket:incrementInUseCount	()V
    //   155: aload_0
    //   156: monitorexit
    //   157: aconst_null
    //   158: astore_3
    //   159: aload_0
    //   160: iload_1
    //   161: invokevirtual 282	com/facebook/imagepipeline/memory/BasePool:alloc	(I)Ljava/lang/Object;
    //   164: astore 4
    //   166: aload 4
    //   168: astore_3
    //   169: goto +39 -> 208
    //   172: astore 4
    //   174: aload_0
    //   175: monitorenter
    //   176: aload_0
    //   177: getfield 108	com/facebook/imagepipeline/memory/BasePool:mUsed	Lcom/facebook/imagepipeline/memory/BasePool$Counter;
    //   180: iload_2
    //   181: invokevirtual 260	com/facebook/imagepipeline/memory/BasePool$Counter:decrement	(I)V
    //   184: aload_0
    //   185: iload_1
    //   186: invokevirtual 243	com/facebook/imagepipeline/memory/BasePool:getBucket	(I)Lcom/facebook/imagepipeline/memory/Bucket;
    //   189: astore 5
    //   191: aload 5
    //   193: ifnull +8 -> 201
    //   196: aload 5
    //   198: invokevirtual 285	com/facebook/imagepipeline/memory/Bucket:decrementInUseCount	()V
    //   201: aload_0
    //   202: monitorexit
    //   203: aload 4
    //   205: invokestatic 291	com/facebook/common/internal/Throwables:propagateIfPossible	(Ljava/lang/Throwable;)V
    //   208: aload_0
    //   209: monitorenter
    //   210: aload_0
    //   211: getfield 103	com/facebook/imagepipeline/memory/BasePool:mInUseValues	Ljava/util/Set;
    //   214: aload_3
    //   215: invokeinterface 250 2 0
    //   220: invokestatic 124	com/facebook/common/internal/Preconditions:checkState	(Z)V
    //   223: aload_0
    //   224: invokevirtual 294	com/facebook/imagepipeline/memory/BasePool:trimToSoftCap	()V
    //   227: aload_0
    //   228: getfield 75	com/facebook/imagepipeline/memory/BasePool:mPoolStatsTracker	Lcom/facebook/imagepipeline/memory/PoolStatsTracker;
    //   231: iload_2
    //   232: invokeinterface 297 2 0
    //   237: aload_0
    //   238: invokespecial 265	com/facebook/imagepipeline/memory/BasePool:logStats	()V
    //   241: iconst_2
    //   242: invokestatic 175	com/facebook/common/logging/FLog:isLoggable	(I)Z
    //   245: ifeq +24 -> 269
    //   248: aload_0
    //   249: getfield 57	com/facebook/imagepipeline/memory/BasePool:TAG	Ljava/lang/Class;
    //   252: ldc_w 299
    //   255: aload_3
    //   256: invokestatic 272	java/lang/System:identityHashCode	(Ljava/lang/Object;)I
    //   259: invokestatic 186	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   262: iload_1
    //   263: invokestatic 186	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   266: invokestatic 275	com/facebook/common/logging/FLog:v	(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V
    //   269: aload_0
    //   270: monitorexit
    //   271: aload_3
    //   272: areturn
    //   273: astore_3
    //   274: aload_0
    //   275: monitorexit
    //   276: aload_3
    //   277: athrow
    //   278: astore_3
    //   279: aload_0
    //   280: monitorexit
    //   281: aload_3
    //   282: athrow
    //   283: new 18	com/facebook/imagepipeline/memory/BasePool$PoolSizeViolationException
    //   286: dup
    //   287: aload_0
    //   288: getfield 71	com/facebook/imagepipeline/memory/BasePool:mPoolParams	Lcom/facebook/imagepipeline/memory/PoolParams;
    //   291: getfield 224	com/facebook/imagepipeline/memory/PoolParams:maxSizeHardCap	I
    //   294: aload_0
    //   295: getfield 108	com/facebook/imagepipeline/memory/BasePool:mUsed	Lcom/facebook/imagepipeline/memory/BasePool$Counter;
    //   298: getfield 120	com/facebook/imagepipeline/memory/BasePool$Counter:mNumBytes	I
    //   301: aload_0
    //   302: getfield 106	com/facebook/imagepipeline/memory/BasePool:mFree	Lcom/facebook/imagepipeline/memory/BasePool$Counter;
    //   305: getfield 120	com/facebook/imagepipeline/memory/BasePool$Counter:mNumBytes	I
    //   308: iload_2
    //   309: invokespecial 302	com/facebook/imagepipeline/memory/BasePool$PoolSizeViolationException:<init>	(IIII)V
    //   312: athrow
    //   313: astore_3
    //   314: aload_0
    //   315: monitorexit
    //   316: aload_3
    //   317: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	318	0	this	BasePool
    //   0	318	1	paramInt	int
    //   60	249	2	i	int
    //   17	255	3	localObject1	Object
    //   273	4	3	localThrowable1	Throwable
    //   278	4	3	localThrowable2	Throwable
    //   313	4	3	localThrowable3	Throwable
    //   27	140	4	localObject2	Object
    //   172	32	4	localThrowable4	Throwable
    //   189	8	5	localBucket	Bucket
    // Exception table:
    //   from	to	target	type
    //   159	166	172	java/lang/Throwable
    //   210	269	273	java/lang/Throwable
    //   269	271	273	java/lang/Throwable
    //   274	276	273	java/lang/Throwable
    //   176	191	278	java/lang/Throwable
    //   196	201	278	java/lang/Throwable
    //   201	203	278	java/lang/Throwable
    //   279	281	278	java/lang/Throwable
    //   12	18	313	java/lang/Throwable
    //   22	29	313	java/lang/Throwable
    //   34	120	313	java/lang/Throwable
    //   120	122	313	java/lang/Throwable
    //   125	147	313	java/lang/Throwable
    //   151	155	313	java/lang/Throwable
    //   155	157	313	java/lang/Throwable
    //   283	313	313	java/lang/Throwable
    //   314	316	313	java/lang/Throwable
  }
  
  Bucket getBucket(int paramInt)
  {
    try
    {
      Bucket localBucket = (Bucket)mBuckets.get(paramInt);
      if ((localBucket == null) && (mAllowNewBuckets))
      {
        if (FLog.isLoggable(2)) {
          FLog.v(TAG, "creating new bucket %s", Integer.valueOf(paramInt));
        }
        localBucket = newBucket(paramInt);
        mBuckets.put(paramInt, localBucket);
        return localBucket;
      }
      return localBucket;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected abstract int getBucketedSize(int paramInt);
  
  protected abstract int getBucketedSizeForValue(Object paramObject);
  
  protected abstract int getSizeInBytes(int paramInt);
  
  public Map getStats()
  {
    try
    {
      HashMap localHashMap = new HashMap();
      int i = 0;
      while (i < mBuckets.size())
      {
        int j = mBuckets.keyAt(i);
        Bucket localBucket = (Bucket)mBuckets.valueAt(i);
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("buckets_used_");
        localStringBuilder.append(getSizeInBytes(j));
        localHashMap.put(localStringBuilder.toString(), Integer.valueOf(localBucket.getInUseCount()));
        i += 1;
      }
      localHashMap.put("soft_cap", Integer.valueOf(mPoolParams.maxSizeSoftCap));
      localHashMap.put("hard_cap", Integer.valueOf(mPoolParams.maxSizeHardCap));
      localHashMap.put("used_count", Integer.valueOf(mUsed.mCount));
      localHashMap.put("used_bytes", Integer.valueOf(mUsed.mNumBytes));
      localHashMap.put("free_count", Integer.valueOf(mFree.mCount));
      localHashMap.put("free_bytes", Integer.valueOf(mFree.mNumBytes));
      return localHashMap;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  protected Object getValue(Bucket paramBucket)
  {
    try
    {
      paramBucket = paramBucket.get();
      return paramBucket;
    }
    catch (Throwable paramBucket)
    {
      throw paramBucket;
    }
  }
  
  protected void initialize()
  {
    mMemoryTrimmableRegistry.registerMemoryTrimmable(this);
    mPoolStatsTracker.setBasePool(this);
  }
  
  boolean isMaxSizeSoftCapExceeded()
  {
    for (;;)
    {
      try
      {
        if (mUsed.mNumBytes + mFree.mNumBytes > mPoolParams.maxSizeSoftCap)
        {
          bool = true;
          if (bool) {
            mPoolStatsTracker.onSoftCapReached();
          }
          return bool;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      boolean bool = false;
    }
  }
  
  protected boolean isReusable(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    return true;
  }
  
  Bucket newBucket(int paramInt)
  {
    return new Bucket(getSizeInBytes(paramInt), Integer.MAX_VALUE, 0, mPoolParams.fixBucketsReinitialization);
  }
  
  protected void onParamsChanged() {}
  
  public void release(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    int i = getBucketedSizeForValue(paramObject);
    int j = getSizeInBytes(i);
    try
    {
      Bucket localBucket = getBucketIfPresent(i);
      if (!mInUseValues.remove(paramObject))
      {
        FLog.e(TAG, "release (free, value unrecognized) (object, size) = (%x, %s)", new Object[] { Integer.valueOf(System.identityHashCode(paramObject)), Integer.valueOf(i) });
        free(paramObject);
        mPoolStatsTracker.onFree(j);
      }
      else if ((localBucket != null) && (!localBucket.isMaxLengthExceeded()) && (!isMaxSizeSoftCapExceeded()) && (isReusable(paramObject)))
      {
        localBucket.release(paramObject);
        mFree.increment(j);
        mUsed.decrement(j);
        mPoolStatsTracker.onValueRelease(j);
        if (FLog.isLoggable(2)) {
          FLog.v(TAG, "release (reuse) (object, size) = (%x, %s)", Integer.valueOf(System.identityHashCode(paramObject)), Integer.valueOf(i));
        }
      }
      else
      {
        if (localBucket != null) {
          localBucket.decrementInUseCount();
        }
        if (FLog.isLoggable(2)) {
          FLog.v(TAG, "release (free) (object, size) = (%x, %s)", Integer.valueOf(System.identityHashCode(paramObject)), Integer.valueOf(i));
        }
        free(paramObject);
        mUsed.decrement(j);
        mPoolStatsTracker.onFree(j);
      }
      logStats();
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public void trim(MemoryTrimType paramMemoryTrimType)
  {
    trimToNothing();
  }
  
  void trimToNothing()
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a7 = a6\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  void trimToSize(int paramInt)
  {
    Object localObject1 = this;
    for (;;)
    {
      int j;
      try
      {
        Object localObject2 = mUsed;
        BasePool localBasePool = this;
        localObject1 = localBasePool;
        int i = mNumBytes;
        localObject1 = localBasePool;
        localObject2 = mFree;
        localObject1 = localBasePool;
        j = mNumBytes;
        localObject1 = localBasePool;
        localObject2 = mFree;
        localObject1 = localBasePool;
        j = Math.min(i + j - paramInt, mNumBytes);
        i = j;
        if (j <= 0) {
          return;
        }
        localObject1 = localBasePool;
        Object localObject3;
        if (FLog.isLoggable(2))
        {
          localObject1 = localBasePool;
          localObject2 = TAG;
          localObject1 = localBasePool;
          localObject3 = mUsed;
          localObject1 = localBasePool;
          int k = mNumBytes;
          localObject1 = localBasePool;
          localObject3 = mFree;
          localObject1 = localBasePool;
          FLog.v((Class)localObject2, "trimToSize: TargetSize = %d; Initial Size = %d; Bytes to free = %d", Integer.valueOf(paramInt), Integer.valueOf(k + mNumBytes), Integer.valueOf(j));
        }
        localBasePool = this;
        localObject1 = localBasePool;
        localBasePool.logStats();
        j = 0;
        localObject1 = localBasePool;
        if ((j < mBuckets.size()) && (i > 0))
        {
          localObject1 = localBasePool;
          localObject2 = (Bucket)mBuckets.valueAt(j);
          if (i > 0)
          {
            localObject1 = localBasePool;
            localObject3 = ((Bucket)localObject2).pop();
            if (localObject3 != null)
            {
              localObject1 = localBasePool;
              localBasePool.free(localObject3);
              localObject1 = localBasePool;
              i -= mItemSize;
              localObject1 = localBasePool;
              localObject3 = mFree;
              localObject1 = localBasePool;
              ((Counter)localObject3).decrement(mItemSize);
              continue;
            }
          }
        }
        else
        {
          localObject1 = localBasePool;
          localBasePool.logStats();
          localObject2 = localBasePool;
          localObject1 = localBasePool;
          if (FLog.isLoggable(2))
          {
            localObject1 = localBasePool;
            localObject2 = TAG;
            localObject1 = localBasePool;
            localObject3 = mUsed;
            localObject1 = localBasePool;
            i = mNumBytes;
            localObject1 = localBasePool;
            localObject3 = mFree;
            localObject1 = localBasePool;
            FLog.v((Class)localObject2, "trimToSize: TargetSize = %d; Final Size = %d", Integer.valueOf(paramInt), Integer.valueOf(i + mNumBytes));
            localObject2 = localBasePool;
          }
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        throw localThrowable;
      }
      j += 1;
    }
  }
  
  void trimToSoftCap()
  {
    try
    {
      if (isMaxSizeSoftCapExceeded()) {
        trimToSize(mPoolParams.maxSizeSoftCap);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  @VisibleForTesting
  @NotThreadSafe
  static class Counter
  {
    private static final String PAGE_KEY = "com.facebook.imagepipeline.memory.BasePool.Counter";
    int mCount;
    int mNumBytes;
    
    Counter() {}
    
    public void decrement(int paramInt)
    {
      if ((mNumBytes >= paramInt) && (mCount > 0))
      {
        mCount -= 1;
        mNumBytes -= paramInt;
        return;
      }
      FLog.wtf("com.facebook.imagepipeline.memory.BasePool.Counter", "Unexpected decrement of %d. Current numBytes = %d, count = %d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(mNumBytes), Integer.valueOf(mCount) });
    }
    
    public void increment(int paramInt)
    {
      mCount += 1;
      mNumBytes += paramInt;
    }
    
    public void reset()
    {
      mCount = 0;
      mNumBytes = 0;
    }
  }
  
  public static class InvalidSizeException
    extends RuntimeException
  {
    public InvalidSizeException(Object paramObject)
    {
      super();
    }
  }
  
  public static class InvalidValueException
    extends RuntimeException
  {
    public InvalidValueException(Object paramObject)
    {
      super();
    }
  }
  
  public static class PoolSizeViolationException
    extends RuntimeException
  {
    public PoolSizeViolationException(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super();
    }
  }
  
  public static class SizeTooLargeException
    extends BasePool.InvalidSizeException
  {
    public SizeTooLargeException(Object paramObject)
    {
      super();
    }
  }
}
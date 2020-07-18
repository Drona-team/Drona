package com.facebook.imagepipeline.cache;

import android.graphics.Bitmap;
import android.os.SystemClock;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Predicate;
import com.facebook.common.internal.Supplier;
import com.facebook.common.internal.VisibleForTesting;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class CountingMemoryCache<K, V>
  implements MemoryCache<K, V>, MemoryTrimmable
{
  private final CacheTrimStrategy mCacheTrimStrategy;
  @VisibleForTesting
  @GuardedBy("this")
  final CountingLruMap<K, Entry<K, V>> mCachedEntries;
  @VisibleForTesting
  @GuardedBy("this")
  final CountingLruMap<K, Entry<K, V>> mExclusiveEntries;
  @GuardedBy("this")
  private long mLastCacheParamsCheck;
  @GuardedBy("this")
  protected MemoryCacheParams mMemoryCacheParams;
  private final Supplier<MemoryCacheParams> mMemoryCacheParamsSupplier;
  @VisibleForTesting
  @GuardedBy("this")
  final Map<Bitmap, Object> mOtherEntries = new WeakHashMap();
  private final ValueDescriptor<V> mValueDescriptor;
  
  public CountingMemoryCache(ValueDescriptor paramValueDescriptor, CacheTrimStrategy paramCacheTrimStrategy, Supplier paramSupplier)
  {
    mValueDescriptor = paramValueDescriptor;
    mExclusiveEntries = new CountingLruMap(wrapValueDescriptor(paramValueDescriptor));
    mCachedEntries = new CountingLruMap(wrapValueDescriptor(paramValueDescriptor));
    mCacheTrimStrategy = paramCacheTrimStrategy;
    mMemoryCacheParamsSupplier = paramSupplier;
    mMemoryCacheParams = ((MemoryCacheParams)mMemoryCacheParamsSupplier.getFolder());
    mLastCacheParamsCheck = SystemClock.uptimeMillis();
  }
  
  private boolean canCacheNewValue(Object paramObject)
  {
    try
    {
      int i = mValueDescriptor.getSizeInBytes(paramObject);
      int j = mMemoryCacheParams.maxCacheEntrySize;
      boolean bool = true;
      if ((i <= j) && (getInUseCount() <= mMemoryCacheParams.maxCacheEntries - 1))
      {
        j = getInUseSizeInBytes();
        int k = mMemoryCacheParams.maxCacheSize;
        if (j <= k - i) {}
      }
      else
      {
        bool = false;
      }
      return bool;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  private void decreaseClientCount(Entry paramEntry)
  {
    for (;;)
    {
      try
      {
        Preconditions.checkNotNull(paramEntry);
        if (clientCount > 0)
        {
          bool = true;
          Preconditions.checkState(bool);
          clientCount -= 1;
          return;
        }
      }
      catch (Throwable paramEntry)
      {
        throw paramEntry;
      }
      boolean bool = false;
    }
  }
  
  private void increaseClientCount(Entry paramEntry)
  {
    try
    {
      Preconditions.checkNotNull(paramEntry);
      Preconditions.checkState(isOrphan ^ true);
      clientCount += 1;
      return;
    }
    catch (Throwable paramEntry)
    {
      throw paramEntry;
    }
  }
  
  private void makeOrphan(Entry paramEntry)
  {
    try
    {
      Preconditions.checkNotNull(paramEntry);
      Preconditions.checkState(isOrphan ^ true);
      isOrphan = true;
      return;
    }
    catch (Throwable paramEntry)
    {
      throw paramEntry;
    }
  }
  
  private void makeOrphans(ArrayList paramArrayList)
  {
    if (paramArrayList != null) {
      try
      {
        paramArrayList = paramArrayList.iterator();
        while (paramArrayList.hasNext()) {
          makeOrphan((Entry)paramArrayList.next());
        }
      }
      catch (Throwable paramArrayList)
      {
        throw paramArrayList;
      }
    }
  }
  
  private boolean maybeAddToExclusives(Entry paramEntry)
  {
    try
    {
      if ((!isOrphan) && (clientCount == 0))
      {
        mExclusiveEntries.removeValue(fieldOwner, paramEntry);
        return true;
      }
      return false;
    }
    catch (Throwable paramEntry)
    {
      throw paramEntry;
    }
  }
  
  private void maybeClose(ArrayList paramArrayList)
  {
    if (paramArrayList != null)
    {
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext()) {
        CloseableReference.closeSafely(referenceToClose((Entry)paramArrayList.next()));
      }
    }
  }
  
  private void maybeEvictEntries()
  {
    try
    {
      ArrayList localArrayList = trimExclusivelyOwnedEntries(Math.min(mMemoryCacheParams.maxEvictionQueueEntries, mMemoryCacheParams.maxCacheEntries - getInUseCount()), Math.min(mMemoryCacheParams.maxEvictionQueueSize, mMemoryCacheParams.maxCacheSize - getInUseSizeInBytes()));
      makeOrphans(localArrayList);
      maybeClose(localArrayList);
      maybeNotifyExclusiveEntryRemoval(localArrayList);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private static void maybeNotifyExclusiveEntryInsertion(Entry paramEntry)
  {
    if ((paramEntry != null) && (observer != null)) {
      observer.onExclusivityChanged(fieldOwner, true);
    }
  }
  
  private static void maybeNotifyExclusiveEntryRemoval(Entry paramEntry)
  {
    if ((paramEntry != null) && (observer != null)) {
      observer.onExclusivityChanged(fieldOwner, false);
    }
  }
  
  private void maybeNotifyExclusiveEntryRemoval(ArrayList paramArrayList)
  {
    if (paramArrayList != null)
    {
      paramArrayList = paramArrayList.iterator();
      while (paramArrayList.hasNext()) {
        maybeNotifyExclusiveEntryRemoval((Entry)paramArrayList.next());
      }
    }
  }
  
  private void maybeUpdateCacheParams()
  {
    try
    {
      long l1 = mLastCacheParamsCheck;
      long l2 = mMemoryCacheParams.paramsCheckIntervalMs;
      long l3 = SystemClock.uptimeMillis();
      if (l1 + l2 > l3) {
        return;
      }
      mLastCacheParamsCheck = SystemClock.uptimeMillis();
      mMemoryCacheParams = ((MemoryCacheParams)mMemoryCacheParamsSupplier.getFolder());
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private CloseableReference newClientReference(final Entry paramEntry)
  {
    try
    {
      increaseClientCount(paramEntry);
      paramEntry = CloseableReference.of(valueRef.get(), new ResourceReleaser()
      {
        public void release(Object paramAnonymousObject)
        {
          CountingMemoryCache.this.releaseClientReference(paramEntry);
        }
      });
      return paramEntry;
    }
    catch (Throwable paramEntry)
    {
      throw paramEntry;
    }
  }
  
  private CloseableReference referenceToClose(Entry paramEntry)
  {
    try
    {
      Preconditions.checkNotNull(paramEntry);
      if ((isOrphan) && (clientCount == 0)) {
        paramEntry = valueRef;
      } else {
        paramEntry = null;
      }
      return paramEntry;
    }
    catch (Throwable paramEntry)
    {
      throw paramEntry;
    }
  }
  
  private void releaseClientReference(Entry paramEntry)
  {
    Preconditions.checkNotNull(paramEntry);
    try
    {
      decreaseClientCount(paramEntry);
      boolean bool = maybeAddToExclusives(paramEntry);
      CloseableReference localCloseableReference = referenceToClose(paramEntry);
      CloseableReference.closeSafely(localCloseableReference);
      if (!bool) {
        paramEntry = null;
      }
      maybeNotifyExclusiveEntryInsertion(paramEntry);
      maybeUpdateCacheParams();
      maybeEvictEntries();
      return;
    }
    catch (Throwable paramEntry)
    {
      throw paramEntry;
    }
  }
  
  /* Error */
  private ArrayList trimExclusivelyOwnedEntries(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: iload_1
    //   3: iconst_0
    //   4: invokestatic 255	java/lang/Math:max	(II)I
    //   7: istore_1
    //   8: iload_2
    //   9: iconst_0
    //   10: invokestatic 255	java/lang/Math:max	(II)I
    //   13: istore_2
    //   14: aload_0
    //   15: getfield 69	com/facebook/imagepipeline/cache/CountingMemoryCache:mExclusiveEntries	Lcom/facebook/imagepipeline/cache/CountingLruMap;
    //   18: invokevirtual 258	com/facebook/imagepipeline/cache/CountingLruMap:getCount	()I
    //   21: iload_1
    //   22: if_icmpgt +20 -> 42
    //   25: aload_0
    //   26: getfield 69	com/facebook/imagepipeline/cache/CountingMemoryCache:mExclusiveEntries	Lcom/facebook/imagepipeline/cache/CountingLruMap;
    //   29: invokevirtual 260	com/facebook/imagepipeline/cache/CountingLruMap:getSizeInBytes	()I
    //   32: istore_3
    //   33: iload_3
    //   34: iload_2
    //   35: if_icmpgt +7 -> 42
    //   38: aload_0
    //   39: monitorexit
    //   40: aconst_null
    //   41: areturn
    //   42: new 151	java/util/ArrayList
    //   45: dup
    //   46: invokespecial 261	java/util/ArrayList:<init>	()V
    //   49: astore 4
    //   51: aload_0
    //   52: getfield 69	com/facebook/imagepipeline/cache/CountingMemoryCache:mExclusiveEntries	Lcom/facebook/imagepipeline/cache/CountingLruMap;
    //   55: invokevirtual 258	com/facebook/imagepipeline/cache/CountingLruMap:getCount	()I
    //   58: iload_1
    //   59: if_icmpgt +24 -> 83
    //   62: aload_0
    //   63: getfield 69	com/facebook/imagepipeline/cache/CountingMemoryCache:mExclusiveEntries	Lcom/facebook/imagepipeline/cache/CountingLruMap;
    //   66: invokevirtual 260	com/facebook/imagepipeline/cache/CountingLruMap:getSizeInBytes	()I
    //   69: istore_3
    //   70: iload_3
    //   71: iload_2
    //   72: if_icmple +6 -> 78
    //   75: goto +8 -> 83
    //   78: aload_0
    //   79: monitorexit
    //   80: aload 4
    //   82: areturn
    //   83: aload_0
    //   84: getfield 69	com/facebook/imagepipeline/cache/CountingMemoryCache:mExclusiveEntries	Lcom/facebook/imagepipeline/cache/CountingLruMap;
    //   87: invokevirtual 264	com/facebook/imagepipeline/cache/CountingLruMap:getFirstKey	()Ljava/lang/Object;
    //   90: astore 5
    //   92: aload_0
    //   93: getfield 69	com/facebook/imagepipeline/cache/CountingMemoryCache:mExclusiveEntries	Lcom/facebook/imagepipeline/cache/CountingLruMap;
    //   96: aload 5
    //   98: invokevirtual 267	com/facebook/imagepipeline/cache/CountingLruMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   101: pop
    //   102: aload 4
    //   104: aload_0
    //   105: getfield 71	com/facebook/imagepipeline/cache/CountingMemoryCache:mCachedEntries	Lcom/facebook/imagepipeline/cache/CountingLruMap;
    //   108: aload 5
    //   110: invokevirtual 267	com/facebook/imagepipeline/cache/CountingLruMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
    //   113: invokevirtual 270	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   116: pop
    //   117: goto -66 -> 51
    //   120: astore 4
    //   122: aload_0
    //   123: monitorexit
    //   124: aload 4
    //   126: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	127	0	this	CountingMemoryCache
    //   0	127	1	paramInt1	int
    //   0	127	2	paramInt2	int
    //   32	41	3	i	int
    //   49	54	4	localArrayList	ArrayList
    //   120	5	4	localThrowable	Throwable
    //   90	19	5	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   2	33	120	java/lang/Throwable
    //   42	51	120	java/lang/Throwable
    //   51	70	120	java/lang/Throwable
    //   83	117	120	java/lang/Throwable
  }
  
  private ValueDescriptor wrapValueDescriptor(final ValueDescriptor paramValueDescriptor)
  {
    new ValueDescriptor()
    {
      public int getSizeInBytes(CountingMemoryCache.Entry paramAnonymousEntry)
      {
        return paramValueDescriptor.getSizeInBytes(valueRef.get());
      }
    };
  }
  
  public CloseableReference cache(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    for (;;)
    {
      try
      {
        Entry localEntry = (Entry)mExclusiveEntries.remove(paramObject);
        paramObject = (Entry)mCachedEntries.map(paramObject);
        if (paramObject != null)
        {
          paramObject = newClientReference(paramObject);
          maybeNotifyExclusiveEntryRemoval(localEntry);
          maybeUpdateCacheParams();
          maybeEvictEntries();
          return paramObject;
        }
      }
      catch (Throwable paramObject)
      {
        throw paramObject;
      }
      paramObject = null;
    }
  }
  
  public CloseableReference cache(Object paramObject, CloseableReference paramCloseableReference)
  {
    return cache(paramObject, paramCloseableReference, null);
  }
  
  public CloseableReference cache(Object paramObject, CloseableReference paramCloseableReference, EntryStateObserver paramEntryStateObserver)
  {
    Preconditions.checkNotNull(paramObject);
    Preconditions.checkNotNull(paramCloseableReference);
    maybeUpdateCacheParams();
    for (;;)
    {
      try
      {
        Entry localEntry = (Entry)mExclusiveEntries.remove(paramObject);
        localObject = (Entry)mCachedEntries.remove(paramObject);
        CloseableReference localCloseableReference = null;
        if (localObject != null)
        {
          makeOrphan((Entry)localObject);
          localObject = referenceToClose((Entry)localObject);
          if (canCacheNewValue(paramCloseableReference.get()))
          {
            paramCloseableReference = Entry.cache(paramObject, paramCloseableReference, paramEntryStateObserver);
            mCachedEntries.removeValue(paramObject, paramCloseableReference);
            localCloseableReference = newClientReference(paramCloseableReference);
          }
          CloseableReference.closeSafely((CloseableReference)localObject);
          maybeNotifyExclusiveEntryRemoval(localEntry);
          maybeEvictEntries();
          return localCloseableReference;
        }
      }
      catch (Throwable paramObject)
      {
        throw paramObject;
      }
      Object localObject = null;
    }
  }
  
  public void clear()
  {
    try
    {
      ArrayList localArrayList1 = mExclusiveEntries.clear();
      ArrayList localArrayList2 = mCachedEntries.clear();
      makeOrphans(localArrayList2);
      maybeClose(localArrayList2);
      maybeNotifyExclusiveEntryRemoval(localArrayList1);
      maybeUpdateCacheParams();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean contains(Predicate paramPredicate)
  {
    try
    {
      boolean bool = mCachedEntries.getMatchingEntries(paramPredicate).isEmpty();
      return bool ^ true;
    }
    catch (Throwable paramPredicate)
    {
      throw paramPredicate;
    }
  }
  
  public boolean contains(Object paramObject)
  {
    try
    {
      boolean bool = mCachedEntries.contains(paramObject);
      return bool;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public int getCount()
  {
    try
    {
      int i = mCachedEntries.getCount();
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getEvictionQueueCount()
  {
    try
    {
      int i = mExclusiveEntries.getCount();
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getEvictionQueueSizeInBytes()
  {
    try
    {
      int i = mExclusiveEntries.getSizeInBytes();
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getInUseCount()
  {
    try
    {
      int i = mCachedEntries.getCount();
      int j = mExclusiveEntries.getCount();
      return i - j;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getInUseSizeInBytes()
  {
    try
    {
      int i = mCachedEntries.getSizeInBytes();
      int j = mExclusiveEntries.getSizeInBytes();
      return i - j;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getSizeInBytes()
  {
    try
    {
      int i = mCachedEntries.getSizeInBytes();
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int removeAll(Predicate paramPredicate)
  {
    try
    {
      ArrayList localArrayList = mExclusiveEntries.removeAll(paramPredicate);
      paramPredicate = mCachedEntries.removeAll(paramPredicate);
      makeOrphans(paramPredicate);
      maybeClose(paramPredicate);
      maybeNotifyExclusiveEntryRemoval(localArrayList);
      maybeUpdateCacheParams();
      maybeEvictEntries();
      return paramPredicate.size();
    }
    catch (Throwable paramPredicate)
    {
      throw paramPredicate;
    }
  }
  
  public CloseableReference reuse(Object paramObject)
  {
    Preconditions.checkNotNull(paramObject);
    for (;;)
    {
      try
      {
        Entry localEntry = (Entry)mExclusiveEntries.remove(paramObject);
        i = 1;
        boolean bool = false;
        if (localEntry == null) {
          break label87;
        }
        paramObject = (Entry)mCachedEntries.remove(paramObject);
        Preconditions.checkNotNull(paramObject);
        if (clientCount == 0) {
          bool = true;
        }
        Preconditions.checkState(bool);
        paramObject = valueRef;
        if (i != 0)
        {
          maybeNotifyExclusiveEntryRemoval(localEntry);
          return paramObject;
        }
      }
      catch (Throwable paramObject)
      {
        throw paramObject;
      }
      return paramObject;
      label87:
      paramObject = null;
      int i = 0;
    }
  }
  
  public void trim(MemoryTrimType paramMemoryTrimType)
  {
    double d = mCacheTrimStrategy.getTrimRatio(paramMemoryTrimType);
    try
    {
      paramMemoryTrimType = trimExclusivelyOwnedEntries(Integer.MAX_VALUE, Math.max(0, (int)(mCachedEntries.getSizeInBytes() * (1.0D - d)) - getInUseSizeInBytes()));
      makeOrphans(paramMemoryTrimType);
      maybeClose(paramMemoryTrimType);
      maybeNotifyExclusiveEntryRemoval(paramMemoryTrimType);
      maybeUpdateCacheParams();
      maybeEvictEntries();
      return;
    }
    catch (Throwable paramMemoryTrimType)
    {
      throw paramMemoryTrimType;
    }
  }
  
  public static abstract interface CacheTrimStrategy
  {
    public abstract double getTrimRatio(MemoryTrimType paramMemoryTrimType);
  }
  
  @VisibleForTesting
  static class Entry<K, V>
  {
    public int clientCount;
    public final K fieldOwner;
    public boolean isOrphan;
    @Nullable
    public final CountingMemoryCache.EntryStateObserver<K> observer;
    public final CloseableReference<V> valueRef;
    
    private Entry(Object paramObject, CloseableReference paramCloseableReference, CountingMemoryCache.EntryStateObserver paramEntryStateObserver)
    {
      fieldOwner = Preconditions.checkNotNull(paramObject);
      valueRef = ((CloseableReference)Preconditions.checkNotNull(CloseableReference.cloneOrNull(paramCloseableReference)));
      clientCount = 0;
      isOrphan = false;
      observer = paramEntryStateObserver;
    }
    
    static Entry cache(Object paramObject, CloseableReference paramCloseableReference, CountingMemoryCache.EntryStateObserver paramEntryStateObserver)
    {
      return new Entry(paramObject, paramCloseableReference, paramEntryStateObserver);
    }
  }
  
  public static abstract interface EntryStateObserver<K>
  {
    public abstract void onExclusivityChanged(Object paramObject, boolean paramBoolean);
  }
}

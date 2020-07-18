package com.google.android.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

public final class SimpleCache
  implements Cache
{
  private static final String TAG = "SimpleCache";
  private static boolean cacheFolderLockingDisabled;
  private static final HashSet<File> lockedCacheDirs = new HashSet();
  private final File cacheDir;
  private final CacheEvictor evictor;
  private final CachedContentIndex index;
  private final HashMap<String, ArrayList<Cache.Listener>> listeners;
  private boolean released;
  private long totalSpace;
  
  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor)
  {
    this(paramFile, paramCacheEvictor, null, false);
  }
  
  SimpleCache(final File paramFile, CacheEvictor paramCacheEvictor, CachedContentIndex paramCachedContentIndex)
  {
    if (lockFolder(paramFile))
    {
      cacheDir = paramFile;
      evictor = paramCacheEvictor;
      index = paramCachedContentIndex;
      listeners = new HashMap();
      paramFile = new ConditionVariable();
      new Thread("SimpleCache.initialize()")
      {
        public void run()
        {
          SimpleCache localSimpleCache = SimpleCache.this;
          try
          {
            paramFile.open();
            SimpleCache.this.initialize();
            evictor.onCacheInitialized();
            return;
          }
          catch (Throwable localThrowable)
          {
            throw localThrowable;
          }
        }
      }.start();
      paramFile.block();
      return;
    }
    paramCacheEvictor = new StringBuilder();
    paramCacheEvictor.append("Another SimpleCache instance uses the folder: ");
    paramCacheEvictor.append(paramFile);
    throw new IllegalStateException(paramCacheEvictor.toString());
  }
  
  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor, byte[] paramArrayOfByte)
  {
    this(paramFile, paramCacheEvictor, paramArrayOfByte, bool);
  }
  
  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor, byte[] paramArrayOfByte, boolean paramBoolean)
  {
    this(paramFile, paramCacheEvictor, new CachedContentIndex(paramFile, paramArrayOfByte, paramBoolean));
  }
  
  private void addSpan(SimpleCacheSpan paramSimpleCacheSpan)
  {
    index.getOrAdd(key).addSpan(paramSimpleCacheSpan);
    totalSpace += length;
    notifySpanAdded(paramSimpleCacheSpan);
  }
  
  public static void disableCacheFolderLocking()
  {
    try
    {
      cacheFolderLockingDisabled = true;
      lockedCacheDirs.clear();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private SimpleCacheSpan getSpan(String paramString, long paramLong)
    throws Cache.CacheException
  {
    CachedContent localCachedContent = index.get(paramString);
    if (localCachedContent == null) {
      return SimpleCacheSpan.createOpenHole(paramString, paramLong);
    }
    for (;;)
    {
      paramString = localCachedContent.getSpan(paramLong);
      if ((!isCached) || (file.exists())) {
        break;
      }
      removeStaleSpansAndCachedContents();
    }
    return paramString;
  }
  
  private void initialize()
  {
    if (!cacheDir.exists())
    {
      cacheDir.mkdirs();
      return;
    }
    index.load();
    File[] arrayOfFile = cacheDir.listFiles();
    if (arrayOfFile == null) {
      return;
    }
    int j = arrayOfFile.length;
    int i = 0;
    while (i < j)
    {
      File localFile = arrayOfFile[i];
      if (!localFile.getName().equals("cached_content_index.exi"))
      {
        if (localFile.length() > 0L) {
          localObject = SimpleCacheSpan.createCacheEntry(localFile, index);
        } else {
          localObject = null;
        }
        if (localObject != null) {
          addSpan((SimpleCacheSpan)localObject);
        } else {
          localFile.delete();
        }
      }
      i += 1;
    }
    index.removeEmpty();
    Object localObject = index;
    try
    {
      ((CachedContentIndex)localObject).store();
      return;
    }
    catch (Cache.CacheException localCacheException)
    {
      Log.e("SimpleCache", "Storing index file failed", localCacheException);
    }
  }
  
  public static boolean isCacheFolderLocked(File paramFile)
  {
    try
    {
      boolean bool = lockedCacheDirs.contains(paramFile.getAbsoluteFile());
      return bool;
    }
    catch (Throwable paramFile)
    {
      throw paramFile;
    }
  }
  
  private static boolean lockFolder(File paramFile)
  {
    try
    {
      boolean bool = cacheFolderLockingDisabled;
      if (bool) {
        return true;
      }
      bool = lockedCacheDirs.add(paramFile.getAbsoluteFile());
      return bool;
    }
    catch (Throwable paramFile)
    {
      throw paramFile;
    }
  }
  
  private void notifySpanAdded(SimpleCacheSpan paramSimpleCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)listeners.get(key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanAdded(this, paramSimpleCacheSpan);
        i -= 1;
      }
    }
    evictor.onSpanAdded(this, paramSimpleCacheSpan);
  }
  
  private void notifySpanRemoved(CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)listeners.get(key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanRemoved(this, paramCacheSpan);
        i -= 1;
      }
    }
    evictor.onSpanRemoved(this, paramCacheSpan);
  }
  
  private void notifySpanTouched(SimpleCacheSpan paramSimpleCacheSpan, CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)listeners.get(key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
        i -= 1;
      }
    }
    evictor.onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
  }
  
  private void removeSpan(CacheSpan paramCacheSpan, boolean paramBoolean)
    throws Cache.CacheException
  {
    CachedContent localCachedContent = index.get(key);
    if (localCachedContent != null)
    {
      if (!localCachedContent.removeSpan(paramCacheSpan)) {
        return;
      }
      totalSpace -= length;
      if (paramBoolean) {
        try
        {
          index.maybeRemove(key);
          index.store();
        }
        catch (Throwable localThrowable)
        {
          notifySpanRemoved(paramCacheSpan);
          throw localThrowable;
        }
      }
      notifySpanRemoved(paramCacheSpan);
    }
  }
  
  private void removeStaleSpansAndCachedContents()
    throws Cache.CacheException
  {
    ArrayList localArrayList = new ArrayList();
    Iterator localIterator1 = index.getAll().iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((CachedContent)localIterator1.next()).getSpans().iterator();
      while (localIterator2.hasNext())
      {
        CacheSpan localCacheSpan = (CacheSpan)localIterator2.next();
        if (!file.exists()) {
          localArrayList.add(localCacheSpan);
        }
      }
    }
    int i = 0;
    while (i < localArrayList.size())
    {
      removeSpan((CacheSpan)localArrayList.get(i), false);
      i += 1;
    }
    index.removeEmpty();
    index.store();
  }
  
  private static void unlockFolder(File paramFile)
  {
    try
    {
      if (!cacheFolderLockingDisabled) {
        lockedCacheDirs.remove(paramFile.getAbsoluteFile());
      }
      return;
    }
    catch (Throwable paramFile)
    {
      throw paramFile;
    }
  }
  
  public NavigableSet addListener(String paramString, Cache.Listener paramListener)
  {
    try
    {
      Assertions.checkState(released ^ true);
      ArrayList localArrayList2 = (ArrayList)listeners.get(paramString);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        listeners.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramListener);
      paramString = getCachedSpans(paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void applyContentMetadataMutations(String paramString, ContentMetadataMutations paramContentMetadataMutations)
    throws Cache.CacheException
  {
    try
    {
      Assertions.checkState(released ^ true);
      index.applyContentMetadataMutations(paramString, paramContentMetadataMutations);
      index.store();
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void commitFile(File paramFile)
    throws Cache.CacheException
  {
    for (;;)
    {
      try
      {
        bool1 = released;
        boolean bool2 = true;
        Assertions.checkState(bool1 ^ true);
        SimpleCacheSpan localSimpleCacheSpan = SimpleCacheSpan.createCacheEntry(paramFile, index);
        if (localSimpleCacheSpan != null)
        {
          bool1 = true;
          Assertions.checkState(bool1);
          CachedContent localCachedContent = index.get(key);
          Assertions.checkNotNull(localCachedContent);
          Assertions.checkState(localCachedContent.isLocked());
          bool1 = paramFile.exists();
          if (!bool1) {
            return;
          }
          if (paramFile.length() == 0L)
          {
            paramFile.delete();
            return;
          }
          long l = ContentMetadataInternal.getContentLength(localCachedContent.getMetadata());
          if (l != -1L)
          {
            if (position + length > l) {
              break label171;
            }
            bool1 = bool2;
            Assertions.checkState(bool1);
          }
          addSpan(localSimpleCacheSpan);
          index.store();
          notifyAll();
          return;
        }
      }
      catch (Throwable paramFile)
      {
        throw paramFile;
      }
      boolean bool1 = false;
      continue;
      label171:
      bool1 = false;
    }
  }
  
  public long getCacheSpace()
  {
    try
    {
      Assertions.checkState(released ^ true);
      long l = totalSpace;
      return l;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public long getCachedLength(String paramString, long paramLong1, long paramLong2)
  {
    try
    {
      Assertions.checkState(released ^ true);
      paramString = index.get(paramString);
      if (paramString != null) {
        paramLong1 = paramString.getCachedBytesLength(paramLong1, paramLong2);
      } else {
        paramLong1 = -paramLong2;
      }
      return paramLong1;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public NavigableSet getCachedSpans(String paramString)
  {
    try
    {
      Assertions.checkState(released ^ true);
      paramString = index.get(paramString);
      if ((paramString != null) && (!paramString.isEmpty())) {
        paramString = new TreeSet(paramString.getSpans());
      } else {
        paramString = new TreeSet();
      }
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public long getContentLength(String paramString)
  {
    try
    {
      long l = ContentMetadataInternal.getContentLength(getContentMetadata(paramString));
      return l;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public ContentMetadata getContentMetadata(String paramString)
  {
    try
    {
      Assertions.checkState(released ^ true);
      paramString = index.getContentMetadata(paramString);
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public Set getKeys()
  {
    try
    {
      Assertions.checkState(released ^ true);
      HashSet localHashSet = new HashSet(index.getKeys());
      return localHashSet;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public boolean isCached(String paramString, long paramLong1, long paramLong2)
  {
    try
    {
      boolean bool2 = released;
      boolean bool1 = true;
      Assertions.checkState(bool2 ^ true);
      paramString = index.get(paramString);
      if (paramString != null)
      {
        paramLong1 = paramString.getCachedBytesLength(paramLong1, paramLong2);
        if (paramLong1 >= paramLong2) {}
      }
      else
      {
        bool1 = false;
      }
      return bool1;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  /* Error */
  public void release()
    throws Cache.CacheException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield 310	com/google/android/exoplayer2/upstream/cache/SimpleCache:released	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +6 -> 14
    //   11: aload_0
    //   12: monitorexit
    //   13: return
    //   14: aload_0
    //   15: getfield 60	com/google/android/exoplayer2/upstream/cache/SimpleCache:listeners	Ljava/util/HashMap;
    //   18: invokevirtual 381	java/util/HashMap:clear	()V
    //   21: aload_0
    //   22: invokespecial 173	com/google/android/exoplayer2/upstream/cache/SimpleCache:removeStaleSpansAndCachedContents	()V
    //   25: aload_0
    //   26: getfield 51	com/google/android/exoplayer2/upstream/cache/SimpleCache:cacheDir	Ljava/io/File;
    //   29: invokestatic 383	com/google/android/exoplayer2/upstream/cache/SimpleCache:unlockFolder	(Ljava/io/File;)V
    //   32: aload_0
    //   33: iconst_1
    //   34: putfield 310	com/google/android/exoplayer2/upstream/cache/SimpleCache:released	Z
    //   37: aload_0
    //   38: monitorexit
    //   39: return
    //   40: astore_2
    //   41: aload_0
    //   42: getfield 51	com/google/android/exoplayer2/upstream/cache/SimpleCache:cacheDir	Ljava/io/File;
    //   45: invokestatic 383	com/google/android/exoplayer2/upstream/cache/SimpleCache:unlockFolder	(Ljava/io/File;)V
    //   48: aload_0
    //   49: iconst_1
    //   50: putfield 310	com/google/android/exoplayer2/upstream/cache/SimpleCache:released	Z
    //   53: aload_2
    //   54: athrow
    //   55: astore_2
    //   56: aload_0
    //   57: monitorexit
    //   58: aload_2
    //   59: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	60	0	this	SimpleCache
    //   6	2	1	bool	boolean
    //   40	14	2	localThrowable1	Throwable
    //   55	4	2	localThrowable2	Throwable
    // Exception table:
    //   from	to	target	type
    //   21	25	40	java/lang/Throwable
    //   2	7	55	java/lang/Throwable
    //   14	21	55	java/lang/Throwable
    //   25	37	55	java/lang/Throwable
    //   41	55	55	java/lang/Throwable
  }
  
  public void releaseHoleSpan(CacheSpan paramCacheSpan)
  {
    try
    {
      Assertions.checkState(released ^ true);
      paramCacheSpan = index.get(key);
      Assertions.checkNotNull(paramCacheSpan);
      Assertions.checkState(paramCacheSpan.isLocked());
      paramCacheSpan.setLocked(false);
      index.maybeRemove(key);
      notifyAll();
      return;
    }
    catch (Throwable paramCacheSpan)
    {
      throw paramCacheSpan;
    }
  }
  
  public void removeListener(String paramString, Cache.Listener paramListener)
  {
    try
    {
      boolean bool = released;
      if (bool) {
        return;
      }
      ArrayList localArrayList = (ArrayList)listeners.get(paramString);
      if (localArrayList != null)
      {
        localArrayList.remove(paramListener);
        if (localArrayList.isEmpty()) {
          listeners.remove(paramString);
        }
      }
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void removeSpan(CacheSpan paramCacheSpan)
    throws Cache.CacheException
  {
    try
    {
      Assertions.checkState(released ^ true);
      removeSpan(paramCacheSpan, true);
      return;
    }
    catch (Throwable paramCacheSpan)
    {
      throw paramCacheSpan;
    }
  }
  
  public void setContentLength(String paramString, long paramLong)
    throws Cache.CacheException
  {
    try
    {
      ContentMetadataMutations localContentMetadataMutations = new ContentMetadataMutations();
      ContentMetadataInternal.setContentLength(localContentMetadataMutations, paramLong);
      applyContentMetadataMutations(paramString, localContentMetadataMutations);
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public File startFile(String paramString, long paramLong1, long paramLong2)
    throws Cache.CacheException
  {
    try
    {
      Assertions.checkState(released ^ true);
      CachedContent localCachedContent = index.get(paramString);
      Assertions.checkNotNull(localCachedContent);
      Assertions.checkState(localCachedContent.isLocked());
      if (!cacheDir.exists())
      {
        cacheDir.mkdirs();
        removeStaleSpansAndCachedContents();
      }
      evictor.onStartFile(this, paramString, paramLong1, paramLong2);
      paramString = SimpleCacheSpan.getCacheFile(cacheDir, length, paramLong1, System.currentTimeMillis());
      return paramString;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  /* Error */
  public SimpleCacheSpan startReadWrite(String paramString, long paramLong)
    throws java.lang.InterruptedException, Cache.CacheException
  {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: aload_1
    //   4: lload_2
    //   5: invokevirtual 431	com/google/android/exoplayer2/upstream/cache/SimpleCache:startReadWriteNonBlocking	(Ljava/lang/String;J)Lcom/google/android/exoplayer2/upstream/cache/SimpleCacheSpan;
    //   8: astore 4
    //   10: aload 4
    //   12: ifnull +8 -> 20
    //   15: aload_0
    //   16: monitorexit
    //   17: aload 4
    //   19: areturn
    //   20: aload_0
    //   21: invokevirtual 434	java/lang/Object:wait	()V
    //   24: goto -22 -> 2
    //   27: astore_1
    //   28: aload_0
    //   29: monitorexit
    //   30: aload_1
    //   31: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	32	0	this	SimpleCache
    //   0	32	1	paramString	String
    //   0	32	2	paramLong	long
    //   8	10	4	localSimpleCacheSpan	SimpleCacheSpan
    // Exception table:
    //   from	to	target	type
    //   2	10	27	java/lang/Throwable
    //   20	24	27	java/lang/Throwable
  }
  
  public SimpleCacheSpan startReadWriteNonBlocking(String paramString, long paramLong)
    throws Cache.CacheException
  {
    try
    {
      Assertions.checkState(released ^ true);
      localSimpleCacheSpan = getSpan(paramString, paramLong);
      boolean bool = isCached;
      if (bool) {
        localCachedContentIndex = index;
      }
    }
    catch (Throwable paramString)
    {
      SimpleCacheSpan localSimpleCacheSpan;
      CachedContentIndex localCachedContentIndex;
      label60:
      throw paramString;
    }
    try
    {
      paramString = localCachedContentIndex.get(paramString).touch(localSimpleCacheSpan);
      notifySpanTouched(localSimpleCacheSpan, paramString);
      return paramString;
    }
    catch (Cache.CacheException paramString)
    {
      break label60;
    }
    return localSimpleCacheSpan;
    paramString = index.getOrAdd(paramString);
    if (!paramString.isLocked())
    {
      paramString.setLocked(true);
      return localSimpleCacheSpan;
    }
    return null;
  }
}

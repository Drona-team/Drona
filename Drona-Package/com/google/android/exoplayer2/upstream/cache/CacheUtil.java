package com.google.android.exoplayer2.upstream.cache;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.PriorityTaskManager;
import com.google.android.exoplayer2.util.PriorityTaskManager.PriorityTooLowException;
import com.google.android.exoplayer2.util.Util;
import java.io.EOFException;
import java.io.IOException;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CacheUtil
{
  public static final int DEFAULT_BUFFER_SIZE_BYTES = 131072;
  public static final CacheKeyFactory DEFAULT_CACHE_KEY_FACTORY = -..Lambda.u97poD-IIwob7OPYcVJkh9jokx0.INSTANCE;
  
  private CacheUtil() {}
  
  public static void cache(DataSpec paramDataSpec, Cache paramCache, DataSource paramDataSource, CachingCounters paramCachingCounters, AtomicBoolean paramAtomicBoolean)
    throws IOException, InterruptedException
  {
    cache(paramDataSpec, paramCache, new CacheDataSource(paramCache, paramDataSource), new byte[131072], null, 0, paramCachingCounters, paramAtomicBoolean, false);
  }
  
  public static void cache(DataSpec paramDataSpec, Cache paramCache, CacheDataSource paramCacheDataSource, byte[] paramArrayOfByte, PriorityTaskManager paramPriorityTaskManager, int paramInt, CachingCounters paramCachingCounters, AtomicBoolean paramAtomicBoolean, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    CachingCounters localCachingCounters = paramCachingCounters;
    Assertions.checkNotNull(paramCacheDataSource);
    Assertions.checkNotNull(paramArrayOfByte);
    if (paramCachingCounters != null) {
      getCached(paramDataSpec, paramCache, paramCachingCounters);
    }
    for (;;)
    {
      break;
      localCachingCounters = new CachingCounters();
    }
    paramCachingCounters = getKey(paramDataSpec);
    long l3 = absoluteStreamPosition;
    if (length != -1L) {
      l1 = length;
    }
    long l2;
    for (long l1 = paramCache.getContentLength(paramCachingCounters); l1 != 0L; l1 -= l2)
    {
      throwExceptionIfInterruptedOrCancelled(paramAtomicBoolean);
      boolean bool = l1 < -1L;
      if (bool) {
        l2 = l1;
      } else {
        l2 = Long.MAX_VALUE;
      }
      l2 = paramCache.getCachedLength(paramCachingCounters, l3, l2);
      if (l2 <= 0L)
      {
        long l4 = -l2;
        l2 = l4;
        if (readAndDiscard(paramDataSpec, l3, l4, paramCacheDataSource, paramArrayOfByte, paramPriorityTaskManager, paramInt, localCachingCounters, paramAtomicBoolean) < l4)
        {
          if (!paramBoolean) {
            break;
          }
          if (!bool) {
            return;
          }
          throw new EOFException();
        }
      }
      l3 += l2;
      if (!bool) {
        l2 = 0L;
      }
    }
  }
  
  public static String generateKey(Uri paramUri)
  {
    return paramUri.toString();
  }
  
  public static void getCached(DataSpec paramDataSpec, Cache paramCache, CachingCounters paramCachingCounters)
  {
    String str = getKey(paramDataSpec);
    long l3 = absoluteStreamPosition;
    long l1;
    if (length != -1L) {
      l1 = length;
    } else {
      l1 = paramCache.getContentLength(str);
    }
    contentLength = l1;
    alreadyCachedBytes = 0L;
    newlyCachedBytes = 0L;
    while (l1 != 0L)
    {
      boolean bool = l1 < -1L;
      if (bool) {
        l2 = l1;
      } else {
        l2 = Long.MAX_VALUE;
      }
      long l4 = paramCache.getCachedLength(str, l3, l2);
      long l2 = l4;
      if (l4 > 0L)
      {
        alreadyCachedBytes += l4;
      }
      else
      {
        l4 = -l4;
        l2 = l4;
        if (l4 == Long.MAX_VALUE) {
          return;
        }
      }
      l3 += l2;
      if (!bool) {
        l2 = 0L;
      }
      l1 -= l2;
    }
  }
  
  public static String getKey(DataSpec paramDataSpec)
  {
    if (key != null) {
      return key;
    }
    return generateKey(uri);
  }
  
  private static long readAndDiscard(DataSpec paramDataSpec, long paramLong1, long paramLong2, DataSource paramDataSource, byte[] paramArrayOfByte, PriorityTaskManager paramPriorityTaskManager, int paramInt, CachingCounters paramCachingCounters, AtomicBoolean paramAtomicBoolean)
    throws IOException, InterruptedException
  {
    for (;;)
    {
      if (paramPriorityTaskManager != null) {
        paramPriorityTaskManager.proceed(paramInt);
      }
      for (;;)
      {
        try
        {
          try
          {
            throwExceptionIfInterruptedOrCancelled(paramAtomicBoolean);
            localObject = uri;
            i = httpMethod;
            byte[] arrayOfByte = httpBody;
            l1 = position;
            l2 = absoluteStreamPosition;
            String str = key;
            int j = flags;
            localObject = new DataSpec((Uri)localObject, i, arrayOfByte, paramLong1, l1 + paramLong1 - l2, -1L, str, j | 0x2);
          }
          catch (Throwable paramDataSpec)
          {
            Object localObject;
            int i;
            long l1;
            long l2;
            Util.closeQuietly(paramDataSource);
            throw paramDataSpec;
          }
        }
        catch (PriorityTaskManager.PriorityTooLowException localPriorityTooLowException)
        {
          continue;
        }
        try
        {
          l1 = paramDataSource.open((DataSpec)localObject);
          l2 = contentLength;
          if ((l2 == -1L) && (l1 != -1L))
          {
            l2 = absoluteStreamPosition;
            contentLength = (l2 + l1);
          }
          l1 = 0L;
          if (l1 != paramLong2)
          {
            throwExceptionIfInterruptedOrCancelled(paramAtomicBoolean);
            if (paramLong2 != -1L)
            {
              l2 = paramArrayOfByte.length;
              l2 = Math.min(l2, paramLong2 - l1);
              i = (int)l2;
            }
            else
            {
              i = paramArrayOfByte.length;
            }
            i = paramDataSource.read(paramArrayOfByte, 0, i);
            if (i == -1)
            {
              paramLong1 = contentLength;
              if (paramLong1 == -1L)
              {
                paramLong1 = absoluteStreamPosition;
                contentLength = (paramLong1 + l1);
              }
            }
            else
            {
              l2 = i;
              l1 += l2;
              long l3 = newlyCachedBytes;
              newlyCachedBytes = (l3 + l2);
              continue;
            }
          }
          Util.closeQuietly(paramDataSource);
          return l1;
        }
        catch (PriorityTaskManager.PriorityTooLowException paramDataSpec) {}
      }
      paramDataSpec = (DataSpec)localObject;
      Util.closeQuietly(paramDataSource);
    }
  }
  
  public static void remove(Cache paramCache, String paramString)
  {
    paramString = paramCache.getCachedSpans(paramString).iterator();
    for (;;)
    {
      CacheSpan localCacheSpan;
      if (paramString.hasNext()) {
        localCacheSpan = (CacheSpan)paramString.next();
      }
      try
      {
        paramCache.removeSpan(localCacheSpan);
      }
      catch (Cache.CacheException localCacheException) {}
      return;
    }
  }
  
  private static void throwExceptionIfInterruptedOrCancelled(AtomicBoolean paramAtomicBoolean)
    throws InterruptedException
  {
    if (!Thread.interrupted())
    {
      if (paramAtomicBoolean == null) {
        return;
      }
      if (!paramAtomicBoolean.get()) {
        return;
      }
    }
    throw new InterruptedException();
  }
  
  public static class CachingCounters
  {
    public volatile long alreadyCachedBytes;
    public volatile long contentLength = -1L;
    public volatile long newlyCachedBytes;
    
    public CachingCounters() {}
    
    public long totalCachedBytes()
    {
      return alreadyCachedBytes + newlyCachedBytes;
    }
  }
}

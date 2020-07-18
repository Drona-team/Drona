package com.bumptech.glide.load.engine.prefill;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.MemoryCache;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.Util;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

final class BitmapPreFillRunner
  implements Runnable
{
  static final int BACKOFF_RATIO = 4;
  private static final Clock DEFAULT_CLOCK = new Clock();
  static final long INITIAL_BACKOFF_MS = 40L;
  static final long MAX_BACKOFF_MS = TimeUnit.SECONDS.toMillis(1L);
  static final long MAX_DURATION_MS = 32L;
  @VisibleForTesting
  static final String TAG = "PreFillRunner";
  private final BitmapPool bitmapPool;
  private final Clock clock;
  private long currentDelay = 40L;
  private final Handler handler;
  private boolean isCancelled;
  private final MemoryCache memoryCache;
  private final Set<PreFillType> seenTypes = new HashSet();
  private final PreFillQueue toPrefill;
  
  public BitmapPreFillRunner(BitmapPool paramBitmapPool, MemoryCache paramMemoryCache, PreFillQueue paramPreFillQueue)
  {
    this(paramBitmapPool, paramMemoryCache, paramPreFillQueue, DEFAULT_CLOCK, new Handler(Looper.getMainLooper()));
  }
  
  BitmapPreFillRunner(BitmapPool paramBitmapPool, MemoryCache paramMemoryCache, PreFillQueue paramPreFillQueue, Clock paramClock, Handler paramHandler)
  {
    bitmapPool = paramBitmapPool;
    memoryCache = paramMemoryCache;
    toPrefill = paramPreFillQueue;
    clock = paramClock;
    handler = paramHandler;
  }
  
  private long getFreeMemoryCacheBytes()
  {
    return memoryCache.getMaxSize() - memoryCache.getCurrentSize();
  }
  
  private long getNextDelay()
  {
    long l = currentDelay;
    currentDelay = Math.min(currentDelay * 4L, MAX_BACKOFF_MS);
    return l;
  }
  
  private boolean isGcDetected(long paramLong)
  {
    return clock.now() - paramLong >= 32L;
  }
  
  boolean allocate()
  {
    long l = clock.now();
    while ((!toPrefill.isEmpty()) && (!isGcDetected(l)))
    {
      PreFillType localPreFillType = toPrefill.remove();
      Object localObject;
      if (!seenTypes.contains(localPreFillType))
      {
        seenTypes.add(localPreFillType);
        localObject = bitmapPool.getDirty(localPreFillType.getWidth(), localPreFillType.getHeight(), localPreFillType.getConfig());
      }
      else
      {
        localObject = Bitmap.createBitmap(localPreFillType.getWidth(), localPreFillType.getHeight(), localPreFillType.getConfig());
      }
      int i = Util.getBitmapByteSize((Bitmap)localObject);
      if (getFreeMemoryCacheBytes() >= i)
      {
        UniqueKey localUniqueKey = new UniqueKey();
        memoryCache.put(localUniqueKey, BitmapResource.obtain((Bitmap)localObject, bitmapPool));
      }
      else
      {
        bitmapPool.put((Bitmap)localObject);
      }
      if (Log.isLoggable("PreFillRunner", 3))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("allocated [");
        ((StringBuilder)localObject).append(localPreFillType.getWidth());
        ((StringBuilder)localObject).append("x");
        ((StringBuilder)localObject).append(localPreFillType.getHeight());
        ((StringBuilder)localObject).append("] ");
        ((StringBuilder)localObject).append(localPreFillType.getConfig());
        ((StringBuilder)localObject).append(" size: ");
        ((StringBuilder)localObject).append(i);
        Log.d("PreFillRunner", ((StringBuilder)localObject).toString());
      }
    }
    return (!isCancelled) && (!toPrefill.isEmpty());
  }
  
  public void cancel()
  {
    isCancelled = true;
  }
  
  public void run()
  {
    if (allocate()) {
      handler.postDelayed(this, getNextDelay());
    }
  }
  
  @VisibleForTesting
  static class Clock
  {
    Clock() {}
    
    long now()
    {
      return SystemClock.currentThreadTimeMillis();
    }
  }
  
  private static final class UniqueKey
    implements Key
  {
    UniqueKey() {}
    
    public void updateDiskCacheKey(MessageDigest paramMessageDigest)
    {
      throw new UnsupportedOperationException();
    }
  }
}

package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Build.VERSION;
import android.util.Log;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LruBitmapPool
  implements BitmapPool
{
  private static final Bitmap.Config DEFAULT_CONFIG = Bitmap.Config.ARGB_8888;
  private static final String TAG = "LruBitmapPool";
  private final Set<Bitmap.Config> allowedConfigs;
  private long currentSize;
  private int evictions;
  private int hits;
  private final long initialMaxSize;
  private long maxSize;
  private int misses;
  private int puts;
  private final LruPoolStrategy strategy;
  private final BitmapTracker tracker;
  
  public LruBitmapPool(long paramLong)
  {
    this(paramLong, getDefaultStrategy(), getDefaultAllowedConfigs());
  }
  
  LruBitmapPool(long paramLong, LruPoolStrategy paramLruPoolStrategy, Set paramSet)
  {
    initialMaxSize = paramLong;
    maxSize = paramLong;
    strategy = paramLruPoolStrategy;
    allowedConfigs = paramSet;
    tracker = new NullBitmapTracker();
  }
  
  public LruBitmapPool(long paramLong, Set paramSet)
  {
    this(paramLong, getDefaultStrategy(), paramSet);
  }
  
  private static void assertNotHardwareConfig(Bitmap.Config paramConfig)
  {
    if (Build.VERSION.SDK_INT < 26) {
      return;
    }
    if (paramConfig != Enum.HARDWARE) {
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Cannot create a mutable Bitmap with config: ");
    localStringBuilder.append(paramConfig);
    localStringBuilder.append(". Consider setting Downsampler#ALLOW_HARDWARE_CONFIG to false in your RequestOptions and/or in GlideBuilder.setDefaultRequestOptions");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  private static Bitmap createBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    if (paramConfig == null) {
      paramConfig = DEFAULT_CONFIG;
    }
    return Bitmap.createBitmap(paramInt1, paramInt2, paramConfig);
  }
  
  private void dump()
  {
    if (Log.isLoggable("LruBitmapPool", 2)) {
      dumpUnchecked();
    }
  }
  
  private void dumpUnchecked()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Hits=");
    localStringBuilder.append(hits);
    localStringBuilder.append(", misses=");
    localStringBuilder.append(misses);
    localStringBuilder.append(", puts=");
    localStringBuilder.append(puts);
    localStringBuilder.append(", evictions=");
    localStringBuilder.append(evictions);
    localStringBuilder.append(", currentSize=");
    localStringBuilder.append(currentSize);
    localStringBuilder.append(", maxSize=");
    localStringBuilder.append(maxSize);
    localStringBuilder.append("\nStrategy=");
    localStringBuilder.append(strategy);
    Log.v("LruBitmapPool", localStringBuilder.toString());
  }
  
  private void evict()
  {
    trimToSize(maxSize);
  }
  
  private static Set getDefaultAllowedConfigs()
  {
    HashSet localHashSet = new HashSet(Arrays.asList(Bitmap.Config.values()));
    if (Build.VERSION.SDK_INT >= 19) {
      localHashSet.add(null);
    }
    if (Build.VERSION.SDK_INT >= 26) {
      localHashSet.remove(Enum.HARDWARE);
    }
    return Collections.unmodifiableSet(localHashSet);
  }
  
  private static LruPoolStrategy getDefaultStrategy()
  {
    if (Build.VERSION.SDK_INT >= 19) {
      return new SizeConfigStrategy();
    }
    return new AttributeStrategy();
  }
  
  private Bitmap getDirtyOrNull(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    try
    {
      assertNotHardwareConfig(paramConfig);
      Object localObject2 = strategy;
      if (paramConfig != null) {
        localObject1 = paramConfig;
      } else {
        localObject1 = DEFAULT_CONFIG;
      }
      Object localObject1 = ((LruPoolStrategy)localObject2).get(paramInt1, paramInt2, (Bitmap.Config)localObject1);
      if (localObject1 == null)
      {
        if (Log.isLoggable("LruBitmapPool", 3))
        {
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append("Missing bitmap=");
          ((StringBuilder)localObject2).append(strategy.logBitmap(paramInt1, paramInt2, paramConfig));
          Log.d("LruBitmapPool", ((StringBuilder)localObject2).toString());
        }
        misses += 1;
      }
      else
      {
        hits += 1;
        currentSize -= strategy.getSize((Bitmap)localObject1);
        tracker.remove((Bitmap)localObject1);
        normalize((Bitmap)localObject1);
      }
      if (Log.isLoggable("LruBitmapPool", 2))
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("Get bitmap=");
        ((StringBuilder)localObject2).append(strategy.logBitmap(paramInt1, paramInt2, paramConfig));
        Log.v("LruBitmapPool", ((StringBuilder)localObject2).toString());
      }
      dump();
      return localObject1;
    }
    catch (Throwable paramConfig)
    {
      throw paramConfig;
    }
  }
  
  private static void maybeSetPreMultiplied(Bitmap paramBitmap)
  {
    if (Build.VERSION.SDK_INT >= 19) {
      paramBitmap.setPremultiplied(true);
    }
  }
  
  private static void normalize(Bitmap paramBitmap)
  {
    paramBitmap.setHasAlpha(true);
    maybeSetPreMultiplied(paramBitmap);
  }
  
  private void trimToSize(long paramLong)
  {
    try
    {
      while (currentSize > paramLong)
      {
        Bitmap localBitmap = strategy.removeLast();
        if (localBitmap == null)
        {
          if (Log.isLoggable("LruBitmapPool", 5))
          {
            Log.w("LruBitmapPool", "Size mismatch, resetting");
            dumpUnchecked();
          }
          currentSize = 0L;
          return;
        }
        tracker.remove(localBitmap);
        currentSize -= strategy.getSize(localBitmap);
        evictions += 1;
        if (Log.isLoggable("LruBitmapPool", 3))
        {
          StringBuilder localStringBuilder = new StringBuilder();
          localStringBuilder.append("Evicting bitmap=");
          localStringBuilder.append(strategy.logBitmap(localBitmap));
          Log.d("LruBitmapPool", localStringBuilder.toString());
        }
        dump();
        localBitmap.recycle();
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void clearMemory()
  {
    if (Log.isLoggable("LruBitmapPool", 3)) {
      Log.d("LruBitmapPool", "clearMemory");
    }
    trimToSize(0L);
  }
  
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    Bitmap localBitmap = getDirtyOrNull(paramInt1, paramInt2, paramConfig);
    if (localBitmap != null)
    {
      localBitmap.eraseColor(0);
      return localBitmap;
    }
    return createBitmap(paramInt1, paramInt2, paramConfig);
  }
  
  public Bitmap getDirty(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    Bitmap localBitmap2 = getDirtyOrNull(paramInt1, paramInt2, paramConfig);
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null) {
      localBitmap1 = createBitmap(paramInt1, paramInt2, paramConfig);
    }
    return localBitmap1;
  }
  
  public long getMaxSize()
  {
    return maxSize;
  }
  
  public void put(Bitmap paramBitmap)
  {
    if (paramBitmap != null) {}
    try
    {
      if (!paramBitmap.isRecycled())
      {
        StringBuilder localStringBuilder;
        if ((paramBitmap.isMutable()) && (strategy.getSize(paramBitmap) <= maxSize) && (allowedConfigs.contains(paramBitmap.getConfig())))
        {
          int i = strategy.getSize(paramBitmap);
          strategy.put(paramBitmap);
          tracker.add(paramBitmap);
          puts += 1;
          currentSize += i;
          if (Log.isLoggable("LruBitmapPool", 2))
          {
            localStringBuilder = new StringBuilder();
            localStringBuilder.append("Put bitmap in pool=");
            localStringBuilder.append(strategy.logBitmap(paramBitmap));
            Log.v("LruBitmapPool", localStringBuilder.toString());
          }
          dump();
          evict();
          return;
        }
        if (Log.isLoggable("LruBitmapPool", 2))
        {
          localStringBuilder = new StringBuilder();
          localStringBuilder.append("Reject bitmap from pool, bitmap: ");
          localStringBuilder.append(strategy.logBitmap(paramBitmap));
          localStringBuilder.append(", is mutable: ");
          localStringBuilder.append(paramBitmap.isMutable());
          localStringBuilder.append(", is allowed config: ");
          localStringBuilder.append(allowedConfigs.contains(paramBitmap.getConfig()));
          Log.v("LruBitmapPool", localStringBuilder.toString());
        }
        paramBitmap.recycle();
        return;
      }
      throw new IllegalStateException("Cannot pool recycled bitmap");
    }
    catch (Throwable paramBitmap)
    {
      throw paramBitmap;
    }
    throw new NullPointerException("Bitmap must not be null");
  }
  
  public void setSizeMultiplier(float paramFloat)
  {
    try
    {
      maxSize = Math.round((float)initialMaxSize * paramFloat);
      evict();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void trimMemory(int paramInt)
  {
    if (Log.isLoggable("LruBitmapPool", 3))
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("trimMemory, level=");
      localStringBuilder.append(paramInt);
      Log.d("LruBitmapPool", localStringBuilder.toString());
    }
    if (paramInt >= 40)
    {
      clearMemory();
      return;
    }
    if ((paramInt >= 20) || (paramInt == 15)) {
      trimToSize(getMaxSize() / 2L);
    }
  }
  
  private static abstract interface BitmapTracker
  {
    public abstract void add(Bitmap paramBitmap);
    
    public abstract void remove(Bitmap paramBitmap);
  }
  
  private static final class NullBitmapTracker
    implements LruBitmapPool.BitmapTracker
  {
    NullBitmapTracker() {}
    
    public void add(Bitmap paramBitmap) {}
    
    public void remove(Bitmap paramBitmap) {}
  }
  
  private static class ThrowingBitmapTracker
    implements LruBitmapPool.BitmapTracker
  {
    private final Set<Bitmap> bitmaps = Collections.synchronizedSet(new HashSet());
    
    private ThrowingBitmapTracker() {}
    
    public void add(Bitmap paramBitmap)
    {
      if (!bitmaps.contains(paramBitmap))
      {
        bitmaps.add(paramBitmap);
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Can't add already added bitmap: ");
      localStringBuilder.append(paramBitmap);
      localStringBuilder.append(" [");
      localStringBuilder.append(paramBitmap.getWidth());
      localStringBuilder.append("x");
      localStringBuilder.append(paramBitmap.getHeight());
      localStringBuilder.append("]");
      throw new IllegalStateException(localStringBuilder.toString());
    }
    
    public void remove(Bitmap paramBitmap)
    {
      if (bitmaps.contains(paramBitmap))
      {
        bitmaps.remove(paramBitmap);
        return;
      }
      throw new IllegalStateException("Cannot remove bitmap not in tracker");
    }
  }
}

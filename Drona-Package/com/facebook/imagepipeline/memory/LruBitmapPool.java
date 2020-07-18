package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmableRegistry;

public class LruBitmapPool
  implements BitmapPool
{
  private int mCurrentSize;
  private int mMaxBitmapSize;
  private final int mMaxPoolSize;
  private final PoolStatsTracker mPoolStatsTracker;
  protected final PoolBackend<Bitmap> mStrategy = new BitmapPoolBackend();
  
  public LruBitmapPool(int paramInt1, int paramInt2, PoolStatsTracker paramPoolStatsTracker, MemoryTrimmableRegistry paramMemoryTrimmableRegistry)
  {
    mMaxPoolSize = paramInt1;
    mMaxBitmapSize = paramInt2;
    mPoolStatsTracker = paramPoolStatsTracker;
    if (paramMemoryTrimmableRegistry != null) {
      paramMemoryTrimmableRegistry.registerMemoryTrimmable(this);
    }
  }
  
  private Bitmap alloc(int paramInt)
  {
    mPoolStatsTracker.onAlloc(paramInt);
    return Bitmap.createBitmap(1, paramInt, Bitmap.Config.ALPHA_8);
  }
  
  private void trimTo(int paramInt)
  {
    try
    {
      while (mCurrentSize > paramInt)
      {
        Bitmap localBitmap = (Bitmap)mStrategy.putIfAbsent();
        if (localBitmap == null) {
          break;
        }
        int i = mStrategy.getSize(localBitmap);
        mCurrentSize -= i;
        mPoolStatsTracker.onFree(i);
      }
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public Bitmap get(int paramInt)
  {
    try
    {
      if (mCurrentSize > mMaxPoolSize) {
        trimTo(mMaxPoolSize);
      }
      Bitmap localBitmap = (Bitmap)mStrategy.get(paramInt);
      if (localBitmap != null)
      {
        paramInt = mStrategy.getSize(localBitmap);
        mCurrentSize -= paramInt;
        mPoolStatsTracker.onValueReuse(paramInt);
        return localBitmap;
      }
      localBitmap = alloc(paramInt);
      return localBitmap;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public void release(Bitmap paramBitmap)
  {
    int i = mStrategy.getSize(paramBitmap);
    if (i <= mMaxBitmapSize)
    {
      mPoolStatsTracker.onValueRelease(i);
      mStrategy.put(paramBitmap);
      try
      {
        mCurrentSize += i;
        return;
      }
      catch (Throwable paramBitmap)
      {
        throw paramBitmap;
      }
    }
  }
  
  public void trim(MemoryTrimType paramMemoryTrimType)
  {
    trimTo((int)(mMaxPoolSize * (1.0D - paramMemoryTrimType.getSuggestedTrimRatio())));
  }
}
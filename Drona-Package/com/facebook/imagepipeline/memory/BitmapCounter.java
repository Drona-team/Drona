package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.imageutils.BitmapUtil;
import javax.annotation.concurrent.GuardedBy;

public class BitmapCounter
{
  @GuardedBy("this")
  private int mCount;
  private final int mMaxCount;
  private final int mMaxSize;
  @GuardedBy("this")
  private long mSize;
  private final ResourceReleaser<Bitmap> mUnpooledBitmapsReleaser;
  
  public BitmapCounter(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    if (paramInt1 > 0) {
      bool1 = true;
    } else {
      bool1 = false;
    }
    Preconditions.checkArgument(bool1);
    boolean bool1 = bool2;
    if (paramInt2 > 0) {
      bool1 = true;
    }
    Preconditions.checkArgument(bool1);
    mMaxCount = paramInt1;
    mMaxSize = paramInt2;
    mUnpooledBitmapsReleaser = new ResourceReleaser()
    {
      public void release(Bitmap paramAnonymousBitmap)
      {
        try
        {
          decrease(paramAnonymousBitmap);
          paramAnonymousBitmap.recycle();
          return;
        }
        catch (Throwable localThrowable)
        {
          paramAnonymousBitmap.recycle();
          throw localThrowable;
        }
      }
    };
  }
  
  public void decrease(Bitmap paramBitmap)
  {
    for (;;)
    {
      try
      {
        int i = BitmapUtil.getSizeInBytes(paramBitmap);
        if (mCount > 0)
        {
          bool = true;
          Preconditions.checkArgument(bool, "No bitmaps registered.");
          long l = i;
          if (l > mSize) {
            break label105;
          }
          bool = true;
          Preconditions.checkArgument(bool, "Bitmap size bigger than the total registered size: %d, %d", new Object[] { Integer.valueOf(i), Long.valueOf(mSize) });
          mSize -= l;
          mCount -= 1;
          return;
        }
      }
      catch (Throwable paramBitmap)
      {
        throw paramBitmap;
      }
      boolean bool = false;
      continue;
      label105:
      bool = false;
    }
  }
  
  public int getCount()
  {
    try
    {
      int i = mCount;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getMaxCount()
  {
    try
    {
      int i = mMaxCount;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public int getMaxSize()
  {
    try
    {
      int i = mMaxSize;
      return i;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public ResourceReleaser getReleaser()
  {
    return mUnpooledBitmapsReleaser;
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
  
  public boolean increase(Bitmap paramBitmap)
  {
    try
    {
      int i = BitmapUtil.getSizeInBytes(paramBitmap);
      if (mCount < mMaxCount)
      {
        long l1 = mSize;
        long l2 = i;
        if (l1 + l2 <= mMaxSize)
        {
          mCount += 1;
          mSize += l2;
          return true;
        }
      }
      return false;
    }
    catch (Throwable paramBitmap)
    {
      throw paramBitmap;
    }
  }
}
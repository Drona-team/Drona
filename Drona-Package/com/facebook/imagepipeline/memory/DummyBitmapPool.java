package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.MemoryTrimType;

public class DummyBitmapPool
  implements BitmapPool
{
  public DummyBitmapPool() {}
  
  public Bitmap get(int paramInt)
  {
    return Bitmap.createBitmap(1, (int)Math.ceil(paramInt / 2.0D), Bitmap.Config.RGB_565);
  }
  
  public void release(Bitmap paramBitmap)
  {
    Preconditions.checkNotNull(paramBitmap);
    paramBitmap.recycle();
  }
  
  public void trim(MemoryTrimType paramMemoryTrimType) {}
}
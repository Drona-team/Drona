package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class BitmapPoolAdapter
  implements BitmapPool
{
  public BitmapPoolAdapter() {}
  
  public void clearMemory() {}
  
  public Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return Bitmap.createBitmap(paramInt1, paramInt2, paramConfig);
  }
  
  public Bitmap getDirty(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return get(paramInt1, paramInt2, paramConfig);
  }
  
  public long getMaxSize()
  {
    return 0L;
  }
  
  public void put(Bitmap paramBitmap)
  {
    paramBitmap.recycle();
  }
  
  public void setSizeMultiplier(float paramFloat) {}
  
  public void trimMemory(int paramInt) {}
}

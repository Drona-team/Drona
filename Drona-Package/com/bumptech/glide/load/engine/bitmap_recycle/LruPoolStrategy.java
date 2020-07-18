package com.bumptech.glide.load.engine.bitmap_recycle;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

abstract interface LruPoolStrategy
{
  public abstract Bitmap get(int paramInt1, int paramInt2, Bitmap.Config paramConfig);
  
  public abstract int getSize(Bitmap paramBitmap);
  
  public abstract String logBitmap(int paramInt1, int paramInt2, Bitmap.Config paramConfig);
  
  public abstract String logBitmap(Bitmap paramBitmap);
  
  public abstract void put(Bitmap paramBitmap);
  
  public abstract Bitmap removeLast();
}

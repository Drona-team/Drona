package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import androidx.annotation.Nullable;
import com.bumptech.glide.gifdecoder.GifDecoder.BitmapProvider;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

public final class GifBitmapProvider
  implements GifDecoder.BitmapProvider
{
  @Nullable
  private final ArrayPool arrayPool;
  private final BitmapPool bitmapPool;
  
  public GifBitmapProvider(BitmapPool paramBitmapPool)
  {
    this(paramBitmapPool, null);
  }
  
  public GifBitmapProvider(BitmapPool paramBitmapPool, ArrayPool paramArrayPool)
  {
    bitmapPool = paramBitmapPool;
    arrayPool = paramArrayPool;
  }
  
  public Bitmap obtain(int paramInt1, int paramInt2, Bitmap.Config paramConfig)
  {
    return bitmapPool.getDirty(paramInt1, paramInt2, paramConfig);
  }
  
  public byte[] obtainByteArray(int paramInt)
  {
    if (arrayPool == null) {
      return new byte[paramInt];
    }
    return (byte[])arrayPool.w(paramInt, [B.class);
  }
  
  public int[] obtainIntArray(int paramInt)
  {
    if (arrayPool == null) {
      return new int[paramInt];
    }
    return (int[])arrayPool.w(paramInt, [I.class);
  }
  
  public void release(Bitmap paramBitmap)
  {
    bitmapPool.put(paramBitmap);
  }
  
  public void release(byte[] paramArrayOfByte)
  {
    if (arrayPool == null) {
      return;
    }
    arrayPool.put(paramArrayOfByte);
  }
  
  public void release(int[] paramArrayOfInt)
  {
    if (arrayPool == null) {
      return;
    }
    arrayPool.put(paramArrayOfInt);
  }
}

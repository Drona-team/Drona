package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;

public class CircleCrop
  extends BitmapTransformation
{
  private static final byte[] ID_BYTES = "com.bumptech.glide.load.resource.bitmap.CircleCrop.1".getBytes(Key.CHARSET);
  private static final String PAGE_KEY = "com.bumptech.glide.load.resource.bitmap.CircleCrop.1";
  private static final int VERSION = 1;
  
  public CircleCrop() {}
  
  public boolean equals(Object paramObject)
  {
    return paramObject instanceof CircleCrop;
  }
  
  public int hashCode()
  {
    return "com.bumptech.glide.load.resource.bitmap.CircleCrop.1".hashCode();
  }
  
  protected Bitmap transform(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return TransformationUtils.circleCrop(paramBitmapPool, paramBitmap, paramInt1, paramInt2);
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(ID_BYTES);
  }
}

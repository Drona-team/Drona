package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;

public class CenterInside
  extends BitmapTransformation
{
  private static final String EVENTLOG_URL = "com.bumptech.glide.load.resource.bitmap.CenterInside";
  private static final byte[] ID_BYTES = "com.bumptech.glide.load.resource.bitmap.CenterInside".getBytes(Key.CHARSET);
  
  public CenterInside() {}
  
  public boolean equals(Object paramObject)
  {
    return paramObject instanceof CenterInside;
  }
  
  public int hashCode()
  {
    return "com.bumptech.glide.load.resource.bitmap.CenterInside".hashCode();
  }
  
  protected Bitmap transform(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return TransformationUtils.centerInside(paramBitmapPool, paramBitmap, paramInt1, paramInt2);
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(ID_BYTES);
  }
}

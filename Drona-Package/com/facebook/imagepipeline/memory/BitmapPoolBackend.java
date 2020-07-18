package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import com.facebook.common.logging.FLog;
import com.facebook.imageutils.BitmapUtil;

public class BitmapPoolBackend
  extends LruBucketsPoolBackend<Bitmap>
{
  private static final String PAGE_KEY = "BitmapPoolBackend";
  
  public BitmapPoolBackend() {}
  
  public Bitmap get(int paramInt)
  {
    Bitmap localBitmap = (Bitmap)super.get(paramInt);
    if ((localBitmap != null) && (isReusable(localBitmap)))
    {
      localBitmap.eraseColor(0);
      return localBitmap;
    }
    return null;
  }
  
  public int getSize(Bitmap paramBitmap)
  {
    return BitmapUtil.getSizeInBytes(paramBitmap);
  }
  
  protected boolean isReusable(Bitmap paramBitmap)
  {
    if (paramBitmap == null) {
      return false;
    }
    if (paramBitmap.isRecycled())
    {
      FLog.wtf("BitmapPoolBackend", "Cannot reuse a recycled bitmap: %s", new Object[] { paramBitmap });
      return false;
    }
    if (!paramBitmap.isMutable())
    {
      FLog.wtf("BitmapPoolBackend", "Cannot reuse an immutable bitmap: %s", new Object[] { paramBitmap });
      return false;
    }
    return true;
  }
  
  public void release(Bitmap paramBitmap)
  {
    if (isReusable(paramBitmap)) {
      super.put(paramBitmap);
    }
  }
}

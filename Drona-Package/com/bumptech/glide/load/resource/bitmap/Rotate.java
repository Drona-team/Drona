package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class Rotate
  extends BitmapTransformation
{
  private static final String EVENTLOG_URL = "com.bumptech.glide.load.resource.bitmap.Rotate";
  private static final byte[] ID_BYTES = "com.bumptech.glide.load.resource.bitmap.Rotate".getBytes(Key.CHARSET);
  private final int degreesToRotate;
  
  public Rotate(int paramInt)
  {
    degreesToRotate = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Rotate))
    {
      paramObject = (Rotate)paramObject;
      if (degreesToRotate == degreesToRotate) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return Util.hashCode("com.bumptech.glide.load.resource.bitmap.Rotate".hashCode(), Util.hashCode(degreesToRotate));
  }
  
  protected Bitmap transform(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return TransformationUtils.rotateImage(paramBitmap, degreesToRotate);
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(ID_BYTES);
    paramMessageDigest.update(ByteBuffer.allocate(4).putInt(degreesToRotate).array());
  }
}

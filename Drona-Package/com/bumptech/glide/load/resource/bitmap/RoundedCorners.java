package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public final class RoundedCorners
  extends BitmapTransformation
{
  private static final byte[] ID_BYTES = "com.bumptech.glide.load.resource.bitmap.RoundedCorners".getBytes(Key.CHARSET);
  private static final String PAGE_KEY = "com.bumptech.glide.load.resource.bitmap.RoundedCorners";
  private final int roundingRadius;
  
  public RoundedCorners(int paramInt)
  {
    boolean bool;
    if (paramInt > 0) {
      bool = true;
    } else {
      bool = false;
    }
    Preconditions.checkArgument(bool, "roundingRadius must be greater than 0.");
    roundingRadius = paramInt;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof RoundedCorners))
    {
      paramObject = (RoundedCorners)paramObject;
      if (roundingRadius == roundingRadius) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    return Util.hashCode("com.bumptech.glide.load.resource.bitmap.RoundedCorners".hashCode(), Util.hashCode(roundingRadius));
  }
  
  protected Bitmap transform(BitmapPool paramBitmapPool, Bitmap paramBitmap, int paramInt1, int paramInt2)
  {
    return TransformationUtils.roundedCorners(paramBitmapPool, paramBitmap, roundingRadius);
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(ID_BYTES);
    paramMessageDigest.update(ByteBuffer.allocate(4).putInt(roundingRadius).array());
  }
}

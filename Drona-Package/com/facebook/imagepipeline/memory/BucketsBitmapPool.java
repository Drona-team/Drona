package com.facebook.imagepipeline.memory;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import javax.annotation.concurrent.ThreadSafe;

@TargetApi(21)
@ThreadSafe
public class BucketsBitmapPool
  extends BasePool<Bitmap>
  implements BitmapPool
{
  public BucketsBitmapPool(MemoryTrimmableRegistry paramMemoryTrimmableRegistry, PoolParams paramPoolParams, PoolStatsTracker paramPoolStatsTracker)
  {
    super(paramMemoryTrimmableRegistry, paramPoolParams, paramPoolStatsTracker);
    initialize();
  }
  
  protected Bitmap alloc(int paramInt)
  {
    return Bitmap.createBitmap(1, (int)Math.ceil(paramInt / 2.0D), Bitmap.Config.RGB_565);
  }
  
  protected void free(Bitmap paramBitmap)
  {
    Preconditions.checkNotNull(paramBitmap);
    paramBitmap.recycle();
  }
  
  protected int getBucketedSize(int paramInt)
  {
    return paramInt;
  }
  
  protected int getBucketedSizeForValue(Bitmap paramBitmap)
  {
    Preconditions.checkNotNull(paramBitmap);
    return paramBitmap.getAllocationByteCount();
  }
  
  protected int getSizeInBytes(int paramInt)
  {
    return paramInt;
  }
  
  protected Bitmap getValue(Bucket paramBucket)
  {
    paramBucket = (Bitmap)super.getValue(paramBucket);
    if (paramBucket != null) {
      paramBucket.eraseColor(0);
    }
    return paramBucket;
  }
  
  protected boolean isReusable(Bitmap paramBitmap)
  {
    Preconditions.checkNotNull(paramBitmap);
    return (!paramBitmap.isRecycled()) && (paramBitmap.isMutable());
  }
}

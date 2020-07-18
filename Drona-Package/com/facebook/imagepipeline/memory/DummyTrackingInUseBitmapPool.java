package com.facebook.imagepipeline.memory;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Sets;
import com.facebook.common.memory.MemoryTrimType;
import java.util.Set;

public class DummyTrackingInUseBitmapPool
  implements BitmapPool
{
  private final Set<Bitmap> mInUseValues = Sets.newIdentityHashSet();
  
  public DummyTrackingInUseBitmapPool() {}
  
  public Bitmap get(int paramInt)
  {
    Bitmap localBitmap = Bitmap.createBitmap(1, (int)Math.ceil(paramInt / 2.0D), Bitmap.Config.RGB_565);
    mInUseValues.add(localBitmap);
    return localBitmap;
  }
  
  public void release(Bitmap paramBitmap)
  {
    Preconditions.checkNotNull(paramBitmap);
    mInUseValues.remove(paramBitmap);
    paramBitmap.recycle();
  }
  
  public void trim(MemoryTrimType paramMemoryTrimType) {}
}

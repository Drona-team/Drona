package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

public class BitmapResource
  implements Resource<Bitmap>, Initializable
{
  private final Bitmap bitmap;
  private final BitmapPool bitmapPool;
  
  public BitmapResource(Bitmap paramBitmap, BitmapPool paramBitmapPool)
  {
    bitmap = ((Bitmap)Preconditions.checkNotNull(paramBitmap, "Bitmap must not be null"));
    bitmapPool = ((BitmapPool)Preconditions.checkNotNull(paramBitmapPool, "BitmapPool must not be null"));
  }
  
  public static BitmapResource obtain(Bitmap paramBitmap, BitmapPool paramBitmapPool)
  {
    if (paramBitmap == null) {
      return null;
    }
    return new BitmapResource(paramBitmap, paramBitmapPool);
  }
  
  public Bitmap get()
  {
    return bitmap;
  }
  
  public Class getResourceClass()
  {
    return Bitmap.class;
  }
  
  public int getSize()
  {
    return Util.getBitmapByteSize(bitmap);
  }
  
  public void initialize()
  {
    bitmap.prepareToDraw();
  }
  
  public void recycle()
  {
    bitmapPool.put(bitmap);
  }
}

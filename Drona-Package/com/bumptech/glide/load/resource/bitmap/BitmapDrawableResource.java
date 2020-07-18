package com.bumptech.glide.load.resource.bitmap;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.drawable.DrawableResource;
import com.bumptech.glide.util.Util;

public class BitmapDrawableResource
  extends DrawableResource<BitmapDrawable>
  implements Initializable
{
  private final BitmapPool bitmapPool;
  
  public BitmapDrawableResource(BitmapDrawable paramBitmapDrawable, BitmapPool paramBitmapPool)
  {
    super(paramBitmapDrawable);
    bitmapPool = paramBitmapPool;
  }
  
  public Class getResourceClass()
  {
    return BitmapDrawable.class;
  }
  
  public int getSize()
  {
    return Util.getBitmapByteSize(((BitmapDrawable)drawable).getBitmap());
  }
  
  public void initialize()
  {
    ((BitmapDrawable)drawable).getBitmap().prepareToDraw();
  }
  
  public void recycle()
  {
    bitmapPool.put(((BitmapDrawable)drawable).getBitmap());
  }
}

package com.bumptech.glide.load.resource.drawable;

import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.engine.Resource;

final class NonOwnedDrawableResource
  extends DrawableResource<Drawable>
{
  private NonOwnedDrawableResource(Drawable paramDrawable)
  {
    super(paramDrawable);
  }
  
  static Resource newInstance(Drawable paramDrawable)
  {
    if (paramDrawable != null) {
      return new NonOwnedDrawableResource(paramDrawable);
    }
    return null;
  }
  
  public Class getResourceClass()
  {
    return drawable.getClass();
  }
  
  public int getSize()
  {
    return Math.max(1, drawable.getIntrinsicWidth() * drawable.getIntrinsicHeight() * 4);
  }
  
  public void recycle() {}
}

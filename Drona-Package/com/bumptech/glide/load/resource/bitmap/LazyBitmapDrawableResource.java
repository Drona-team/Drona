package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Initializable;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;

public final class LazyBitmapDrawableResource
  implements Resource<BitmapDrawable>, Initializable
{
  private final Resource<Bitmap> bitmapResource;
  private final Resources resources;
  
  private LazyBitmapDrawableResource(Resources paramResources, Resource paramResource)
  {
    resources = ((Resources)Preconditions.checkNotNull(paramResources));
    bitmapResource = ((Resource)Preconditions.checkNotNull(paramResource));
  }
  
  public static Resource obtain(Resources paramResources, Resource paramResource)
  {
    if (paramResource == null) {
      return null;
    }
    return new LazyBitmapDrawableResource(paramResources, paramResource);
  }
  
  public static LazyBitmapDrawableResource obtain(Context paramContext, Bitmap paramBitmap)
  {
    return (LazyBitmapDrawableResource)obtain(paramContext.getResources(), BitmapResource.obtain(paramBitmap, Glide.get(paramContext).getBitmapPool()));
  }
  
  public static LazyBitmapDrawableResource obtain(Resources paramResources, BitmapPool paramBitmapPool, Bitmap paramBitmap)
  {
    return (LazyBitmapDrawableResource)obtain(paramResources, BitmapResource.obtain(paramBitmap, paramBitmapPool));
  }
  
  public BitmapDrawable get()
  {
    return new BitmapDrawable(resources, (Bitmap)bitmapResource.get());
  }
  
  public Class getResourceClass()
  {
    return BitmapDrawable.class;
  }
  
  public int getSize()
  {
    return bitmapResource.getSize();
  }
  
  public void initialize()
  {
    if ((bitmapResource instanceof Initializable)) {
      ((Initializable)bitmapResource).initialize();
    }
  }
  
  public void recycle()
  {
    bitmapResource.recycle();
  }
}

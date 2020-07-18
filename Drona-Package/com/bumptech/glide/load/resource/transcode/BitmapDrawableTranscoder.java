package com.bumptech.glide.load.resource.transcode;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.LazyBitmapDrawableResource;
import com.bumptech.glide.util.Preconditions;

public class BitmapDrawableTranscoder
  implements ResourceTranscoder<Bitmap, BitmapDrawable>
{
  private final Resources resources;
  
  public BitmapDrawableTranscoder(Context paramContext)
  {
    this(paramContext.getResources());
  }
  
  public BitmapDrawableTranscoder(Resources paramResources)
  {
    resources = ((Resources)Preconditions.checkNotNull(paramResources));
  }
  
  public BitmapDrawableTranscoder(Resources paramResources, BitmapPool paramBitmapPool)
  {
    this(paramResources);
  }
  
  public Resource transcode(Resource paramResource, Options paramOptions)
  {
    return LazyBitmapDrawableResource.obtain(resources, paramResource);
  }
}

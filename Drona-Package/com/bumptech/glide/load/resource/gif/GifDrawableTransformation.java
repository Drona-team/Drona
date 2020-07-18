package com.bumptech.glide.load.resource.gif;

import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public class GifDrawableTransformation
  implements Transformation<GifDrawable>
{
  private final Transformation<Bitmap> wrapped;
  
  public GifDrawableTransformation(Transformation paramTransformation)
  {
    wrapped = ((Transformation)Preconditions.checkNotNull(paramTransformation));
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof GifDrawableTransformation))
    {
      paramObject = (GifDrawableTransformation)paramObject;
      return wrapped.equals(wrapped);
    }
    return false;
  }
  
  public int hashCode()
  {
    return wrapped.hashCode();
  }
  
  public Resource transform(Context paramContext, Resource paramResource, int paramInt1, int paramInt2)
  {
    GifDrawable localGifDrawable = (GifDrawable)paramResource.get();
    Object localObject = Glide.get(paramContext).getBitmapPool();
    localObject = new BitmapResource(localGifDrawable.getFirstFrame(), (BitmapPool)localObject);
    paramContext = wrapped.transform(paramContext, (Resource)localObject, paramInt1, paramInt2);
    if (!localObject.equals(paramContext)) {
      ((Resource)localObject).recycle();
    }
    paramContext = (Bitmap)paramContext.get();
    localGifDrawable.setFrameTransformation(wrapped, paramContext);
    return paramResource;
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    wrapped.updateDiskCacheKey(paramMessageDigest);
  }
}

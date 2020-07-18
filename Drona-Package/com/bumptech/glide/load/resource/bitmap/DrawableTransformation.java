package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import java.security.MessageDigest;

public class DrawableTransformation
  implements Transformation<Drawable>
{
  private final boolean isRequired;
  private final Transformation<Bitmap> wrapped;
  
  public DrawableTransformation(Transformation paramTransformation, boolean paramBoolean)
  {
    wrapped = paramTransformation;
    isRequired = paramBoolean;
  }
  
  private Resource newDrawableResource(Context paramContext, Resource paramResource)
  {
    return LazyBitmapDrawableResource.obtain(paramContext.getResources(), paramResource);
  }
  
  public Transformation asBitmapDrawable()
  {
    return this;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DrawableTransformation))
    {
      paramObject = (DrawableTransformation)paramObject;
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
    Object localObject2 = Glide.get(paramContext).getBitmapPool();
    Object localObject1 = (Drawable)paramResource.get();
    localObject2 = DrawableToBitmapConverter.convert((BitmapPool)localObject2, (Drawable)localObject1, paramInt1, paramInt2);
    if (localObject2 == null)
    {
      if (!isRequired) {
        return paramResource;
      }
      paramContext = new StringBuilder();
      paramContext.append("Unable to convert ");
      paramContext.append(localObject1);
      paramContext.append(" to a Bitmap");
      throw new IllegalArgumentException(paramContext.toString());
    }
    localObject1 = wrapped.transform(paramContext, (Resource)localObject2, paramInt1, paramInt2);
    if (localObject1.equals(localObject2))
    {
      ((Resource)localObject1).recycle();
      return paramResource;
    }
    return newDrawableResource(paramContext, (Resource)localObject1);
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    wrapped.updateDiskCacheKey(paramMessageDigest);
  }
}

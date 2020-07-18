package com.bumptech.glide.load.resource.bitmap;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

@Deprecated
public class BitmapDrawableTransformation
  implements Transformation<BitmapDrawable>
{
  private final Transformation<Drawable> wrapped;
  
  public BitmapDrawableTransformation(Transformation paramTransformation)
  {
    wrapped = ((Transformation)Preconditions.checkNotNull(new DrawableTransformation(paramTransformation, false)));
  }
  
  private static Resource convertToBitmapDrawableResource(Resource paramResource)
  {
    if ((paramResource.get() instanceof BitmapDrawable)) {
      return paramResource;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Wrapped transformation unexpectedly returned a non BitmapDrawable resource: ");
    localStringBuilder.append(paramResource.get());
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  private static Resource convertToDrawableResource(Resource paramResource)
  {
    return paramResource;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof BitmapDrawableTransformation))
    {
      paramObject = (BitmapDrawableTransformation)paramObject;
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
    paramResource = convertToDrawableResource(paramResource);
    return convertToBitmapDrawableResource(wrapped.transform(paramContext, paramResource, paramInt1, paramInt2));
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    wrapped.updateDiskCacheKey(paramMessageDigest);
  }
}

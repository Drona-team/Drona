package com.bumptech.glide.load.resource;

import android.content.Context;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import java.security.MessageDigest;

public final class UnitTransformation<T>
  implements Transformation<T>
{
  private static final Transformation<?> TRANSFORMATION = new UnitTransformation();
  
  private UnitTransformation() {}
  
  public static UnitTransformation get()
  {
    return (UnitTransformation)TRANSFORMATION;
  }
  
  public Resource transform(Context paramContext, Resource paramResource, int paramInt1, int paramInt2)
  {
    return paramResource;
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest) {}
}

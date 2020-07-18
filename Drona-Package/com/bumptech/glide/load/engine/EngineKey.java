package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;
import java.util.Map;

class EngineKey
  implements Key
{
  private int hashCode;
  private final int height;
  private final Object model;
  private final Options options;
  private final Class<?> resourceClass;
  private final Key signature;
  private final Class<?> transcodeClass;
  private final Map<Class<?>, Transformation<?>> transformations;
  private final int width;
  
  EngineKey(Object paramObject, Key paramKey, int paramInt1, int paramInt2, Map paramMap, Class paramClass1, Class paramClass2, Options paramOptions)
  {
    model = Preconditions.checkNotNull(paramObject);
    signature = ((Key)Preconditions.checkNotNull(paramKey, "Signature must not be null"));
    width = paramInt1;
    height = paramInt2;
    transformations = ((Map)Preconditions.checkNotNull(paramMap));
    resourceClass = ((Class)Preconditions.checkNotNull(paramClass1, "Resource class must not be null"));
    transcodeClass = ((Class)Preconditions.checkNotNull(paramClass2, "Transcode class must not be null"));
    options = ((Options)Preconditions.checkNotNull(paramOptions));
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof EngineKey))
    {
      paramObject = (EngineKey)paramObject;
      if ((model.equals(model)) && (signature.equals(signature)) && (height == height) && (width == width) && (transformations.equals(transformations)) && (resourceClass.equals(resourceClass)) && (transcodeClass.equals(transcodeClass)) && (options.equals(options))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    if (hashCode == 0)
    {
      hashCode = model.hashCode();
      hashCode = (hashCode * 31 + signature.hashCode());
      hashCode = (hashCode * 31 + width);
      hashCode = (hashCode * 31 + height);
      hashCode = (hashCode * 31 + transformations.hashCode());
      hashCode = (hashCode * 31 + resourceClass.hashCode());
      hashCode = (hashCode * 31 + transcodeClass.hashCode());
      hashCode = (hashCode * 31 + options.hashCode());
    }
    return hashCode;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("EngineKey{model=");
    localStringBuilder.append(model);
    localStringBuilder.append(", width=");
    localStringBuilder.append(width);
    localStringBuilder.append(", height=");
    localStringBuilder.append(height);
    localStringBuilder.append(", resourceClass=");
    localStringBuilder.append(resourceClass);
    localStringBuilder.append(", transcodeClass=");
    localStringBuilder.append(transcodeClass);
    localStringBuilder.append(", signature=");
    localStringBuilder.append(signature);
    localStringBuilder.append(", hashCode=");
    localStringBuilder.append(hashCode);
    localStringBuilder.append(", transformations=");
    localStringBuilder.append(transformations);
    localStringBuilder.append(", options=");
    localStringBuilder.append(options);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    throw new UnsupportedOperationException();
  }
}

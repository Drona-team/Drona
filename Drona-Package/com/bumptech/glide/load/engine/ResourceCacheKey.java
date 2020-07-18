package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.bitmap_recycle.ArrayPool;
import com.bumptech.glide.util.LruCache;
import com.bumptech.glide.util.Util;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

final class ResourceCacheKey
  implements Key
{
  private static final LruCache<Class<?>, byte[]> RESOURCE_CLASS_BYTES = new LruCache(50L);
  private final ArrayPool arrayPool;
  private final Class<?> decodedResourceClass;
  private final int height;
  private final Options options;
  private final Key signature;
  private final Key sourceKey;
  private final Transformation<?> transformation;
  private final int width;
  
  ResourceCacheKey(ArrayPool paramArrayPool, Key paramKey1, Key paramKey2, int paramInt1, int paramInt2, Transformation paramTransformation, Class paramClass, Options paramOptions)
  {
    arrayPool = paramArrayPool;
    sourceKey = paramKey1;
    signature = paramKey2;
    width = paramInt1;
    height = paramInt2;
    transformation = paramTransformation;
    decodedResourceClass = paramClass;
    options = paramOptions;
  }
  
  private byte[] getResourceClassBytes()
  {
    byte[] arrayOfByte2 = (byte[])RESOURCE_CLASS_BYTES.put(decodedResourceClass);
    byte[] arrayOfByte1 = arrayOfByte2;
    if (arrayOfByte2 == null)
    {
      arrayOfByte1 = decodedResourceClass.getName().getBytes(Key.CHARSET);
      RESOURCE_CLASS_BYTES.put(decodedResourceClass, arrayOfByte1);
    }
    return arrayOfByte1;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ResourceCacheKey))
    {
      paramObject = (ResourceCacheKey)paramObject;
      if ((height == height) && (width == width) && (Util.bothNullOrEqual(transformation, transformation)) && (decodedResourceClass.equals(decodedResourceClass)) && (sourceKey.equals(sourceKey)) && (signature.equals(signature)) && (options.equals(options))) {
        return true;
      }
    }
    return false;
  }
  
  public int hashCode()
  {
    int j = ((sourceKey.hashCode() * 31 + signature.hashCode()) * 31 + width) * 31 + height;
    int i = j;
    if (transformation != null) {
      i = j * 31 + transformation.hashCode();
    }
    return (i * 31 + decodedResourceClass.hashCode()) * 31 + options.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ResourceCacheKey{sourceKey=");
    localStringBuilder.append(sourceKey);
    localStringBuilder.append(", signature=");
    localStringBuilder.append(signature);
    localStringBuilder.append(", width=");
    localStringBuilder.append(width);
    localStringBuilder.append(", height=");
    localStringBuilder.append(height);
    localStringBuilder.append(", decodedResourceClass=");
    localStringBuilder.append(decodedResourceClass);
    localStringBuilder.append(", transformation='");
    localStringBuilder.append(transformation);
    localStringBuilder.append('\'');
    localStringBuilder.append(", options=");
    localStringBuilder.append(options);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    byte[] arrayOfByte = (byte[])arrayPool.getExact(8, [B.class);
    ByteBuffer.wrap(arrayOfByte).putInt(width).putInt(height).array();
    signature.updateDiskCacheKey(paramMessageDigest);
    sourceKey.updateDiskCacheKey(paramMessageDigest);
    paramMessageDigest.update(arrayOfByte);
    if (transformation != null) {
      transformation.updateDiskCacheKey(paramMessageDigest);
    }
    options.updateDiskCacheKey(paramMessageDigest);
    paramMessageDigest.update(getResourceClassBytes());
    arrayPool.put(arrayOfByte);
  }
}

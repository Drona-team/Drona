package com.bumptech.glide.signature;

import com.bumptech.glide.load.Key;
import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public final class ObjectKey
  implements Key
{
  private final Object object;
  
  public ObjectKey(Object paramObject)
  {
    object = Preconditions.checkNotNull(paramObject);
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof ObjectKey))
    {
      paramObject = (ObjectKey)paramObject;
      return object.equals(object);
    }
    return false;
  }
  
  public int hashCode()
  {
    return object.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("ObjectKey{object=");
    localStringBuilder.append(object);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    paramMessageDigest.update(object.toString().getBytes(Key.CHARSET));
  }
}

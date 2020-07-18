package com.bumptech.glide.load.engine;

import com.bumptech.glide.load.Key;
import java.security.MessageDigest;

final class DataCacheKey
  implements Key
{
  private final Key signature;
  private final Key sourceKey;
  
  DataCacheKey(Key paramKey1, Key paramKey2)
  {
    sourceKey = paramKey1;
    signature = paramKey2;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof DataCacheKey))
    {
      paramObject = (DataCacheKey)paramObject;
      if ((sourceKey.equals(sourceKey)) && (signature.equals(signature))) {
        return true;
      }
    }
    return false;
  }
  
  Key getSourceKey()
  {
    return sourceKey;
  }
  
  public int hashCode()
  {
    return sourceKey.hashCode() * 31 + signature.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("DataCacheKey{sourceKey=");
    localStringBuilder.append(sourceKey);
    localStringBuilder.append(", signature=");
    localStringBuilder.append(signature);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    sourceKey.updateDiskCacheKey(paramMessageDigest);
    signature.updateDiskCacheKey(paramMessageDigest);
  }
}

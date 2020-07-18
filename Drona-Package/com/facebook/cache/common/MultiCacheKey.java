package com.facebook.cache.common;

import android.net.Uri;
import com.facebook.common.internal.Preconditions;
import java.util.List;

public class MultiCacheKey
  implements CacheKey
{
  final List<CacheKey> mCacheKeys;
  
  public MultiCacheKey(List paramList)
  {
    mCacheKeys = ((List)Preconditions.checkNotNull(paramList));
  }
  
  public boolean containsUri(Uri paramUri)
  {
    int i = 0;
    while (i < mCacheKeys.size())
    {
      if (((CacheKey)mCacheKeys.get(i)).containsUri(paramUri)) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == this) {
      return true;
    }
    if ((paramObject instanceof MultiCacheKey))
    {
      paramObject = (MultiCacheKey)paramObject;
      return mCacheKeys.equals(mCacheKeys);
    }
    return false;
  }
  
  public List getCacheKeys()
  {
    return mCacheKeys;
  }
  
  public String getUriString()
  {
    return ((CacheKey)mCacheKeys.get(0)).getUriString();
  }
  
  public int hashCode()
  {
    return mCacheKeys.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("MultiCacheKey:");
    localStringBuilder.append(mCacheKeys.toString());
    return localStringBuilder.toString();
  }
}

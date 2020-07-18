package com.facebook.cache.common;

import com.facebook.common.util.SecureHashUtil;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public final class CacheKeyUtil
{
  public CacheKeyUtil() {}
  
  public static String getFirstResourceId(CacheKey paramCacheKey)
  {
    if ((paramCacheKey instanceof MultiCacheKey)) {
      paramCacheKey = (MultiCacheKey)paramCacheKey;
    }
    try
    {
      paramCacheKey = paramCacheKey.getCacheKeys().get(0);
      paramCacheKey = (CacheKey)paramCacheKey;
      paramCacheKey = secureHashKey(paramCacheKey);
      return paramCacheKey;
    }
    catch (UnsupportedEncodingException paramCacheKey)
    {
      throw new RuntimeException(paramCacheKey);
    }
    paramCacheKey = secureHashKey(paramCacheKey);
    return paramCacheKey;
  }
  
  public static List getResourceIds(CacheKey paramCacheKey)
  {
    if ((paramCacheKey instanceof MultiCacheKey)) {
      paramCacheKey = (MultiCacheKey)paramCacheKey;
    }
    ArrayList localArrayList;
    try
    {
      paramCacheKey = paramCacheKey.getCacheKeys();
      localArrayList = new ArrayList(paramCacheKey.size());
      int i = 0;
      for (;;)
      {
        int j = paramCacheKey.size();
        if (i >= j) {
          break;
        }
        Object localObject = paramCacheKey.get(i);
        localObject = (CacheKey)localObject;
        localArrayList.add(secureHashKey((CacheKey)localObject));
        i += 1;
      }
      localArrayList = new ArrayList(1);
      localArrayList.add(secureHashKey(paramCacheKey));
      return localArrayList;
    }
    catch (UnsupportedEncodingException paramCacheKey)
    {
      throw new RuntimeException(paramCacheKey);
    }
    return localArrayList;
  }
  
  private static String secureHashKey(CacheKey paramCacheKey)
    throws UnsupportedEncodingException
  {
    return SecureHashUtil.makeSHA1HashBase64(paramCacheKey.getUriString().getBytes("UTF-8"));
  }
}

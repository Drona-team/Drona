package com.bumptech.glide.load;

import com.bumptech.glide.util.Preconditions;
import java.security.MessageDigest;

public final class Option<T>
{
  private static final CacheKeyUpdater<Object> EMPTY_UPDATER = new CacheKeyUpdater()
  {
    public void update(byte[] paramAnonymousArrayOfByte, Object paramAnonymousObject, MessageDigest paramAnonymousMessageDigest) {}
  };
  private final CacheKeyUpdater<T> cacheKeyUpdater;
  private final T defaultValue;
  private final String key;
  private volatile byte[] keyBytes;
  
  private Option(String paramString, Object paramObject, CacheKeyUpdater paramCacheKeyUpdater)
  {
    key = Preconditions.checkNotEmpty(paramString);
    defaultValue = paramObject;
    cacheKeyUpdater = ((CacheKeyUpdater)Preconditions.checkNotNull(paramCacheKeyUpdater));
  }
  
  public static Option disk(String paramString, CacheKeyUpdater paramCacheKeyUpdater)
  {
    return new Option(paramString, null, paramCacheKeyUpdater);
  }
  
  public static Option disk(String paramString, Object paramObject, CacheKeyUpdater paramCacheKeyUpdater)
  {
    return new Option(paramString, paramObject, paramCacheKeyUpdater);
  }
  
  private static CacheKeyUpdater emptyUpdater()
  {
    return EMPTY_UPDATER;
  }
  
  private byte[] getKeyBytes()
  {
    if (keyBytes == null) {
      keyBytes = key.getBytes(Key.CHARSET);
    }
    return keyBytes;
  }
  
  public static Option memory(String paramString)
  {
    return new Option(paramString, null, emptyUpdater());
  }
  
  public static Option memory(String paramString, Object paramObject)
  {
    return new Option(paramString, paramObject, emptyUpdater());
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Option))
    {
      paramObject = (Option)paramObject;
      return key.equals(key);
    }
    return false;
  }
  
  public Object getDefaultValue()
  {
    return defaultValue;
  }
  
  public int hashCode()
  {
    return key.hashCode();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Option{key='");
    localStringBuilder.append(key);
    localStringBuilder.append('\'');
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void update(Object paramObject, MessageDigest paramMessageDigest)
  {
    cacheKeyUpdater.update(getKeyBytes(), paramObject, paramMessageDigest);
  }
  
  public static abstract interface CacheKeyUpdater<T>
  {
    public abstract void update(byte[] paramArrayOfByte, Object paramObject, MessageDigest paramMessageDigest);
  }
}

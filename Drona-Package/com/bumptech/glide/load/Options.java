package com.bumptech.glide.load;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import com.bumptech.glide.util.CachedHashCodeArrayMap;
import java.security.MessageDigest;

public final class Options
  implements Key
{
  private final ArrayMap<Option<?>, Object> values = new CachedHashCodeArrayMap();
  
  public Options() {}
  
  private static void updateDiskCacheKey(Option paramOption, Object paramObject, MessageDigest paramMessageDigest)
  {
    paramOption.update(paramObject, paramMessageDigest);
  }
  
  public Options add(Option paramOption, Object paramObject)
  {
    values.put(paramOption, paramObject);
    return this;
  }
  
  public boolean equals(Object paramObject)
  {
    if ((paramObject instanceof Options))
    {
      paramObject = (Options)paramObject;
      return values.equals(values);
    }
    return false;
  }
  
  public Object getOption(Option paramOption)
  {
    if (values.containsKey(paramOption)) {
      return values.get(paramOption);
    }
    return paramOption.getDefaultValue();
  }
  
  public int hashCode()
  {
    return values.hashCode();
  }
  
  public void putAll(Options paramOptions)
  {
    values.putAll(values);
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Options{values=");
    localStringBuilder.append(values);
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public void updateDiskCacheKey(MessageDigest paramMessageDigest)
  {
    int i = 0;
    while (i < values.size())
    {
      updateDiskCacheKey((Option)values.keyAt(i), values.valueAt(i), paramMessageDigest);
      i += 1;
    }
  }
}

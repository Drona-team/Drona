package com.bumptech.glide.util;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;

public final class CachedHashCodeArrayMap<K, V>
  extends ArrayMap<K, V>
{
  private int hashCode;
  
  public CachedHashCodeArrayMap() {}
  
  public void clear()
  {
    hashCode = 0;
    super.clear();
  }
  
  public int hashCode()
  {
    if (hashCode == 0) {
      hashCode = super.hashCode();
    }
    return hashCode;
  }
  
  public Object put(Object paramObject1, Object paramObject2)
  {
    hashCode = 0;
    return super.put(paramObject1, paramObject2);
  }
  
  public void putAll(SimpleArrayMap paramSimpleArrayMap)
  {
    hashCode = 0;
    super.putAll(paramSimpleArrayMap);
  }
  
  public Object removeAt(int paramInt)
  {
    hashCode = 0;
    return super.removeAt(paramInt);
  }
  
  public Object setValueAt(int paramInt, Object paramObject)
  {
    hashCode = 0;
    return super.setValueAt(paramInt, paramObject);
  }
}

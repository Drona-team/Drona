package com.facebook.react.common;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder
{
  public MapBuilder() {}
  
  public static Builder builder()
  {
    return new Builder(null);
  }
  
  public static Map create()
  {
    return newHashMap();
  }
  
  public static Map get(Object paramObject1, Object paramObject2)
  {
    Map localMap = create();
    localMap.put(paramObject1, paramObject2);
    return localMap;
  }
  
  public static Map get(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    Map localMap = create();
    localMap.put(paramObject1, paramObject2);
    localMap.put(paramObject3, paramObject4);
    return localMap;
  }
  
  public static Map get(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6)
  {
    Map localMap = create();
    localMap.put(paramObject1, paramObject2);
    localMap.put(paramObject3, paramObject4);
    localMap.put(paramObject5, paramObject6);
    return localMap;
  }
  
  public static Map get(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8)
  {
    Map localMap = create();
    localMap.put(paramObject1, paramObject2);
    localMap.put(paramObject3, paramObject4);
    localMap.put(paramObject5, paramObject6);
    localMap.put(paramObject7, paramObject8);
    return localMap;
  }
  
  public static Map get(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12)
  {
    Map localMap = create();
    localMap.put(paramObject1, paramObject2);
    localMap.put(paramObject3, paramObject4);
    localMap.put(paramObject5, paramObject6);
    localMap.put(paramObject7, paramObject8);
    localMap.put(paramObject9, paramObject10);
    localMap.put(paramObject11, paramObject12);
    return localMap;
  }
  
  public static Map get(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10, Object paramObject11, Object paramObject12, Object paramObject13, Object paramObject14)
  {
    Map localMap = create();
    localMap.put(paramObject1, paramObject2);
    localMap.put(paramObject3, paramObject4);
    localMap.put(paramObject5, paramObject6);
    localMap.put(paramObject7, paramObject8);
    localMap.put(paramObject9, paramObject10);
    localMap.put(paramObject11, paramObject12);
    localMap.put(paramObject13, paramObject14);
    return localMap;
  }
  
  public static HashMap newHashMap()
  {
    return new HashMap();
  }
  
  public static Map of(Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4, Object paramObject5, Object paramObject6, Object paramObject7, Object paramObject8, Object paramObject9, Object paramObject10)
  {
    Map localMap = create();
    localMap.put(paramObject1, paramObject2);
    localMap.put(paramObject3, paramObject4);
    localMap.put(paramObject5, paramObject6);
    localMap.put(paramObject7, paramObject8);
    localMap.put(paramObject9, paramObject10);
    return localMap;
  }
  
  public static final class Builder<K, V>
  {
    private Map mMap = MapBuilder.newHashMap();
    private boolean mUnderConstruction = true;
    
    private Builder() {}
    
    public Map build()
    {
      if (mUnderConstruction)
      {
        mUnderConstruction = false;
        return mMap;
      }
      throw new IllegalStateException("Underlying map has already been built");
    }
    
    public Builder put(Object paramObject1, Object paramObject2)
    {
      if (mUnderConstruction)
      {
        mMap.put(paramObject1, paramObject2);
        return this;
      }
      throw new IllegalStateException("Underlying map has already been built");
    }
  }
}
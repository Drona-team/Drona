package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import androidx.core.util.Pools.SimplePool;

public class DynamicFromMap
  implements Dynamic
{
  private static final ThreadLocal<Pools.SimplePool<DynamicFromMap>> sPool = new ThreadLocal()
  {
    protected Pools.SimplePool initialValue()
    {
      return new Pools.SimplePool(10);
    }
  };
  @Nullable
  private ReadableMap mMap;
  @Nullable
  private String mName;
  
  private DynamicFromMap() {}
  
  public static DynamicFromMap create(ReadableMap paramReadableMap, String paramString)
  {
    DynamicFromMap localDynamicFromMap2 = (DynamicFromMap)((Pools.SimplePool)sPool.get()).acquire();
    DynamicFromMap localDynamicFromMap1 = localDynamicFromMap2;
    if (localDynamicFromMap2 == null) {
      localDynamicFromMap1 = new DynamicFromMap();
    }
    mMap = paramReadableMap;
    mName = paramString;
    return localDynamicFromMap1;
  }
  
  public ReadableArray asArray()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.getArray(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public boolean asBoolean()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.getBoolean(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public double asDouble()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.getDouble(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public int asInt()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.getInt(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public ReadableMap asMap()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.getMap(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public String asString()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.getString(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public ReadableType getType()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.getType(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public boolean isNull()
  {
    if ((mMap != null) && (mName != null)) {
      return mMap.isNull(mName);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public void recycle()
  {
    mMap = null;
    mName = null;
    ((Pools.SimplePool)sPool.get()).release(this);
  }
}

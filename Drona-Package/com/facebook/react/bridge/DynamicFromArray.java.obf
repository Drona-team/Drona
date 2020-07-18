package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import androidx.core.util.Pools.SimplePool;

public class DynamicFromArray
  implements Dynamic
{
  private static final Pools.SimplePool<DynamicFromArray> sPool = new Pools.SimplePool(10);
  @Nullable
  private ReadableArray mArray;
  private int mIndex = -1;
  
  private DynamicFromArray() {}
  
  public static DynamicFromArray create(ReadableArray paramReadableArray, int paramInt)
  {
    DynamicFromArray localDynamicFromArray2 = (DynamicFromArray)sPool.acquire();
    DynamicFromArray localDynamicFromArray1 = localDynamicFromArray2;
    if (localDynamicFromArray2 == null) {
      localDynamicFromArray1 = new DynamicFromArray();
    }
    mArray = paramReadableArray;
    mIndex = paramInt;
    return localDynamicFromArray1;
  }
  
  public ReadableArray asArray()
  {
    if (mArray != null) {
      return mArray.getArray(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public boolean asBoolean()
  {
    if (mArray != null) {
      return mArray.getBoolean(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public double asDouble()
  {
    if (mArray != null) {
      return mArray.getDouble(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public int asInt()
  {
    if (mArray != null) {
      return mArray.getInt(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public ReadableMap asMap()
  {
    if (mArray != null) {
      return mArray.getMap(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public String asString()
  {
    if (mArray != null) {
      return mArray.getString(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public ReadableType getType()
  {
    if (mArray != null) {
      return mArray.getType(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public boolean isNull()
  {
    if (mArray != null) {
      return mArray.isNull(mIndex);
    }
    throw new IllegalStateException("This dynamic value has been recycled");
  }
  
  public void recycle()
  {
    mArray = null;
    mIndex = -1;
    sPool.release(this);
  }
}

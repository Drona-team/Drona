package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.upgrade.HybridData;
import java.util.ArrayList;
import java.util.Arrays;

@DoNotStrip
public class ReadableNativeArray
  extends NativeArray
  implements ReadableArray
{
  private static int jniPassCounter = 0;
  @Nullable
  private Object[] mLocalArray;
  @Nullable
  private ReadableType[] mLocalTypeArray;
  
  static {}
  
  protected ReadableNativeArray(HybridData paramHybridData)
  {
    super(paramHybridData);
  }
  
  public static int getJNIPassCounter()
  {
    return jniPassCounter;
  }
  
  private Object[] getLocalArray()
  {
    if (mLocalArray != null) {
      return mLocalArray;
    }
    try
    {
      if (mLocalArray == null)
      {
        jniPassCounter += 1;
        mLocalArray = ((Object[])Assertions.assertNotNull(importArray()));
      }
      return mLocalArray;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private ReadableType[] getLocalTypeArray()
  {
    if (mLocalTypeArray != null) {
      return mLocalTypeArray;
    }
    try
    {
      if (mLocalTypeArray == null)
      {
        jniPassCounter += 1;
        Object[] arrayOfObject = (Object[])Assertions.assertNotNull(importTypeArray());
        mLocalTypeArray = ((ReadableType[])Arrays.copyOf(arrayOfObject, arrayOfObject.length, [Lcom.facebook.react.bridge.ReadableType.class));
      }
      return mLocalTypeArray;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private native Object[] importArray();
  
  private native Object[] importTypeArray();
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ReadableNativeArray)) {
      return false;
    }
    paramObject = (ReadableNativeArray)paramObject;
    return Arrays.deepEquals(getLocalArray(), paramObject.getLocalArray());
  }
  
  public ReadableNativeArray getArray(int paramInt)
  {
    return (ReadableNativeArray)getLocalArray()[paramInt];
  }
  
  public boolean getBoolean(int paramInt)
  {
    return ((Boolean)getLocalArray()[paramInt]).booleanValue();
  }
  
  public double getDouble(int paramInt)
  {
    return ((Double)getLocalArray()[paramInt]).doubleValue();
  }
  
  public Dynamic getDynamic(int paramInt)
  {
    return DynamicFromArray.create(this, paramInt);
  }
  
  public int getInt(int paramInt)
  {
    return ((Double)getLocalArray()[paramInt]).intValue();
  }
  
  public ReadableNativeMap getMap(int paramInt)
  {
    return (ReadableNativeMap)getLocalArray()[paramInt];
  }
  
  public String getString(int paramInt)
  {
    return (String)getLocalArray()[paramInt];
  }
  
  public ReadableType getType(int paramInt)
  {
    return getLocalTypeArray()[paramInt];
  }
  
  public int hashCode()
  {
    return getLocalArray().hashCode();
  }
  
  public boolean isNull(int paramInt)
  {
    return getLocalArray()[paramInt] == null;
  }
  
  public int size()
  {
    return getLocalArray().length;
  }
  
  public ArrayList toArrayList()
  {
    Object localObject = new ArrayList();
    int i = 0;
    while (i < size())
    {
      switch (1.$SwitchMap$com$facebook$react$bridge$ReadableType[getType(i).ordinal()])
      {
      default: 
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Could not convert object at index: ");
        ((StringBuilder)localObject).append(i);
        ((StringBuilder)localObject).append(".");
        throw new IllegalArgumentException(((StringBuilder)localObject).toString());
      case 6: 
        ((ArrayList)localObject).add(getArray(i).toArrayList());
        break;
      case 5: 
        ((ArrayList)localObject).add(getMap(i).toHashMap());
        break;
      case 4: 
        ((ArrayList)localObject).add(getString(i));
        break;
      case 3: 
        ((ArrayList)localObject).add(Double.valueOf(getDouble(i)));
        break;
      case 2: 
        ((ArrayList)localObject).add(Boolean.valueOf(getBoolean(i)));
        break;
      case 1: 
        ((ArrayList)localObject).add(null);
      }
      i += 1;
    }
    return localObject;
  }
}

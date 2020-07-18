package com.facebook.react.bridge;

import com.facebook.infer.annotation.Assertions;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.upgrade.HybridData;

@DoNotStrip
public class WritableNativeMap
  extends ReadableNativeMap
  implements WritableMap
{
  static {}
  
  public WritableNativeMap()
  {
    super(initHybrid());
  }
  
  private static native HybridData initHybrid();
  
  private native void mergeNativeMap(ReadableNativeMap paramReadableNativeMap);
  
  private native void putNativeArray(String paramString, WritableNativeArray paramWritableNativeArray);
  
  private native void putNativeMap(String paramString, WritableNativeMap paramWritableNativeMap);
  
  public WritableMap copy()
  {
    WritableNativeMap localWritableNativeMap = new WritableNativeMap();
    localWritableNativeMap.merge(this);
    return localWritableNativeMap;
  }
  
  public void merge(ReadableMap paramReadableMap)
  {
    Assertions.assertCondition(paramReadableMap instanceof ReadableNativeMap, "Illegal type provided");
    mergeNativeMap((ReadableNativeMap)paramReadableMap);
  }
  
  public void putArray(String paramString, ReadableArray paramReadableArray)
  {
    boolean bool;
    if ((paramReadableArray != null) && (!(paramReadableArray instanceof WritableNativeArray))) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.assertCondition(bool, "Illegal type provided");
    putNativeArray(paramString, (WritableNativeArray)paramReadableArray);
  }
  
  public native void putBoolean(String paramString, boolean paramBoolean);
  
  public native void putDouble(String paramString, double paramDouble);
  
  public native void putInt(String paramString, int paramInt);
  
  public void putMap(String paramString, ReadableMap paramReadableMap)
  {
    boolean bool;
    if ((paramReadableMap != null) && (!(paramReadableMap instanceof WritableNativeMap))) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.assertCondition(bool, "Illegal type provided");
    putNativeMap(paramString, (WritableNativeMap)paramReadableMap);
  }
  
  public native void putNull(String paramString);
  
  public native void putString(String paramString1, String paramString2);
}

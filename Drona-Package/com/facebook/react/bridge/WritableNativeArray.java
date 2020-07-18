package com.facebook.react.bridge;

import com.facebook.infer.annotation.Assertions;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.upgrade.HybridData;

@DoNotStrip
public class WritableNativeArray
  extends ReadableNativeArray
  implements WritableArray
{
  static {}
  
  public WritableNativeArray()
  {
    super(initHybrid());
  }
  
  private static native HybridData initHybrid();
  
  private native void pushNativeArray(WritableNativeArray paramWritableNativeArray);
  
  private native void pushNativeMap(WritableNativeMap paramWritableNativeMap);
  
  public void pushArray(ReadableArray paramReadableArray)
  {
    boolean bool;
    if ((paramReadableArray != null) && (!(paramReadableArray instanceof WritableNativeArray))) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.assertCondition(bool, "Illegal type provided");
    pushNativeArray((WritableNativeArray)paramReadableArray);
  }
  
  public native void pushBoolean(boolean paramBoolean);
  
  public native void pushDouble(double paramDouble);
  
  public native void pushInt(int paramInt);
  
  public void pushMap(ReadableMap paramReadableMap)
  {
    boolean bool;
    if ((paramReadableMap != null) && (!(paramReadableMap instanceof WritableNativeMap))) {
      bool = false;
    } else {
      bool = true;
    }
    Assertions.assertCondition(bool, "Illegal type provided");
    pushNativeMap((WritableNativeMap)paramReadableMap);
  }
  
  public native void pushNull();
  
  public native void pushString(String paramString);
}

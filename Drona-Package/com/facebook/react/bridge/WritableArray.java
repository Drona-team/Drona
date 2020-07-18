package com.facebook.react.bridge;

public abstract interface WritableArray
  extends ReadableArray
{
  public abstract void pushArray(ReadableArray paramReadableArray);
  
  public abstract void pushBoolean(boolean paramBoolean);
  
  public abstract void pushDouble(double paramDouble);
  
  public abstract void pushInt(int paramInt);
  
  public abstract void pushMap(ReadableMap paramReadableMap);
  
  public abstract void pushNull();
  
  public abstract void pushString(String paramString);
}

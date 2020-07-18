package com.facebook.react.bridge;

public abstract interface WritableMap
  extends ReadableMap
{
  public abstract WritableMap copy();
  
  public abstract void merge(ReadableMap paramReadableMap);
  
  public abstract void putArray(String paramString, ReadableArray paramReadableArray);
  
  public abstract void putBoolean(String paramString, boolean paramBoolean);
  
  public abstract void putDouble(String paramString, double paramDouble);
  
  public abstract void putInt(String paramString, int paramInt);
  
  public abstract void putMap(String paramString, ReadableMap paramReadableMap);
  
  public abstract void putNull(String paramString);
  
  public abstract void putString(String paramString1, String paramString2);
}

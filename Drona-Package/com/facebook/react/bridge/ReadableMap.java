package com.facebook.react.bridge;

import java.util.HashMap;
import java.util.Iterator;

public abstract interface ReadableMap
{
  public abstract ReadableArray getArray(String paramString);
  
  public abstract boolean getBoolean(String paramString);
  
  public abstract double getDouble(String paramString);
  
  public abstract Dynamic getDynamic(String paramString);
  
  public abstract Iterator getEntryIterator();
  
  public abstract int getInt(String paramString);
  
  public abstract ReadableMap getMap(String paramString);
  
  public abstract String getString(String paramString);
  
  public abstract ReadableType getType(String paramString);
  
  public abstract boolean hasKey(String paramString);
  
  public abstract boolean isNull(String paramString);
  
  public abstract ReadableMapKeySetIterator keySetIterator();
  
  public abstract HashMap toHashMap();
}

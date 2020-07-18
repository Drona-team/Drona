package com.facebook.react.uimanager;

import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import java.util.Map;

public class ReactStylesDiffMap
{
  final ReadableMap mBackingMap;
  
  public ReactStylesDiffMap(ReadableMap paramReadableMap)
  {
    mBackingMap = paramReadableMap;
  }
  
  public ReadableArray getArray(String paramString)
  {
    return mBackingMap.getArray(paramString);
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    if (mBackingMap.isNull(paramString)) {
      return paramBoolean;
    }
    return mBackingMap.getBoolean(paramString);
  }
  
  public double getDouble(String paramString, double paramDouble)
  {
    if (mBackingMap.isNull(paramString)) {
      return paramDouble;
    }
    return mBackingMap.getDouble(paramString);
  }
  
  public Dynamic getDynamic(String paramString)
  {
    return mBackingMap.getDynamic(paramString);
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    if (mBackingMap.isNull(paramString)) {
      return paramFloat;
    }
    return (float)mBackingMap.getDouble(paramString);
  }
  
  public int getInt(String paramString, int paramInt)
  {
    if (mBackingMap.isNull(paramString)) {
      return paramInt;
    }
    return mBackingMap.getInt(paramString);
  }
  
  public ReadableMap getMap(String paramString)
  {
    return mBackingMap.getMap(paramString);
  }
  
  public String getString(String paramString)
  {
    return mBackingMap.getString(paramString);
  }
  
  public boolean hasKey(String paramString)
  {
    return mBackingMap.hasKey(paramString);
  }
  
  public boolean isNull(String paramString)
  {
    return mBackingMap.isNull(paramString);
  }
  
  public Map toMap()
  {
    return mBackingMap.toHashMap();
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("{ ");
    localStringBuilder.append(getClass().getSimpleName());
    localStringBuilder.append(": ");
    localStringBuilder.append(mBackingMap.toString());
    localStringBuilder.append(" }");
    return localStringBuilder.toString();
  }
}

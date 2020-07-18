package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import com.facebook.common.logging.FLog;

public class DynamicFromObject
  implements Dynamic
{
  @Nullable
  private Object mObject;
  
  public DynamicFromObject(Object paramObject)
  {
    mObject = paramObject;
  }
  
  public ReadableArray asArray()
  {
    return (ReadableArray)mObject;
  }
  
  public boolean asBoolean()
  {
    return ((Boolean)mObject).booleanValue();
  }
  
  public double asDouble()
  {
    return ((Double)mObject).doubleValue();
  }
  
  public int asInt()
  {
    return ((Double)mObject).intValue();
  }
  
  public ReadableMap asMap()
  {
    return (ReadableMap)mObject;
  }
  
  public String asString()
  {
    return (String)mObject;
  }
  
  public ReadableType getType()
  {
    if (isNull()) {
      return ReadableType.Null;
    }
    if ((mObject instanceof Boolean)) {
      return ReadableType.Boolean;
    }
    if ((mObject instanceof Number)) {
      return ReadableType.Number;
    }
    if ((mObject instanceof String)) {
      return ReadableType.String;
    }
    if ((mObject instanceof ReadableMap)) {
      return ReadableType.GIF;
    }
    if ((mObject instanceof ReadableArray)) {
      return ReadableType.Array;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Unmapped object type ");
    localStringBuilder.append(mObject.getClass().getName());
    FLog.e("ReactNative", localStringBuilder.toString());
    return ReadableType.Null;
  }
  
  public boolean isNull()
  {
    return mObject == null;
  }
  
  public void recycle() {}
}

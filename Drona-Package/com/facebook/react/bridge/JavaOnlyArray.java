package com.facebook.react.bridge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaOnlyArray
  implements ReadableArray, WritableArray
{
  private final List mBackingList;
  
  public JavaOnlyArray()
  {
    mBackingList = new ArrayList();
  }
  
  private JavaOnlyArray(List paramList)
  {
    mBackingList = new ArrayList(paramList);
  }
  
  private JavaOnlyArray(Object... paramVarArgs)
  {
    mBackingList = Arrays.asList(paramVarArgs);
  }
  
  public static JavaOnlyArray deepClone(ReadableArray paramReadableArray)
  {
    JavaOnlyArray localJavaOnlyArray = new JavaOnlyArray();
    int j = paramReadableArray.size();
    int i = 0;
    while (i < j)
    {
      ReadableType localReadableType = paramReadableArray.getType(i);
      switch (1.$SwitchMap$com$facebook$react$bridge$ReadableType[localReadableType.ordinal()])
      {
      default: 
        break;
      case 6: 
        localJavaOnlyArray.pushArray(deepClone(paramReadableArray.getArray(i)));
        break;
      case 5: 
        localJavaOnlyArray.pushMap(JavaOnlyMap.deepClone(paramReadableArray.getMap(i)));
        break;
      case 4: 
        localJavaOnlyArray.pushString(paramReadableArray.getString(i));
        break;
      case 3: 
        localJavaOnlyArray.pushDouble(paramReadableArray.getDouble(i));
        break;
      case 2: 
        localJavaOnlyArray.pushBoolean(paramReadableArray.getBoolean(i));
        break;
      case 1: 
        localJavaOnlyArray.pushNull();
      }
      i += 1;
    }
    return localJavaOnlyArray;
  }
  
  public static JavaOnlyArray deleteInTx(Object... paramVarArgs)
  {
    return new JavaOnlyArray(paramVarArgs);
  }
  
  public static JavaOnlyArray from(List paramList)
  {
    return new JavaOnlyArray(paramList);
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if (paramObject != null)
    {
      if (getClass() != paramObject.getClass()) {
        return false;
      }
      paramObject = (JavaOnlyArray)paramObject;
      if (mBackingList != null)
      {
        if (!mBackingList.equals(mBackingList)) {
          return false;
        }
      }
      else
      {
        if (mBackingList == null) {
          break label67;
        }
        return false;
      }
      return true;
    }
    else
    {
      return false;
    }
    label67:
    return true;
  }
  
  public ReadableArray getArray(int paramInt)
  {
    return (ReadableArray)mBackingList.get(paramInt);
  }
  
  public boolean getBoolean(int paramInt)
  {
    return ((Boolean)mBackingList.get(paramInt)).booleanValue();
  }
  
  public double getDouble(int paramInt)
  {
    return ((Number)mBackingList.get(paramInt)).doubleValue();
  }
  
  public Dynamic getDynamic(int paramInt)
  {
    return DynamicFromArray.create(this, paramInt);
  }
  
  public int getInt(int paramInt)
  {
    return ((Number)mBackingList.get(paramInt)).intValue();
  }
  
  public ReadableMap getMap(int paramInt)
  {
    return (ReadableMap)mBackingList.get(paramInt);
  }
  
  public String getString(int paramInt)
  {
    return (String)mBackingList.get(paramInt);
  }
  
  public ReadableType getType(int paramInt)
  {
    Object localObject = mBackingList.get(paramInt);
    if (localObject == null) {
      return ReadableType.Null;
    }
    if ((localObject instanceof Boolean)) {
      return ReadableType.Boolean;
    }
    if ((!(localObject instanceof Double)) && (!(localObject instanceof Float)) && (!(localObject instanceof Integer)))
    {
      if ((localObject instanceof String)) {
        return ReadableType.String;
      }
      if ((localObject instanceof ReadableArray)) {
        return ReadableType.Array;
      }
      if ((localObject instanceof ReadableMap)) {
        return ReadableType.GIF;
      }
      return null;
    }
    return ReadableType.Number;
  }
  
  public int hashCode()
  {
    if (mBackingList != null) {
      return mBackingList.hashCode();
    }
    return 0;
  }
  
  public boolean isNull(int paramInt)
  {
    return mBackingList.get(paramInt) == null;
  }
  
  public void pushArray(ReadableArray paramReadableArray)
  {
    mBackingList.add(paramReadableArray);
  }
  
  public void pushBoolean(boolean paramBoolean)
  {
    mBackingList.add(Boolean.valueOf(paramBoolean));
  }
  
  public void pushDouble(double paramDouble)
  {
    mBackingList.add(Double.valueOf(paramDouble));
  }
  
  public void pushInt(int paramInt)
  {
    mBackingList.add(new Double(paramInt));
  }
  
  public void pushMap(ReadableMap paramReadableMap)
  {
    mBackingList.add(paramReadableMap);
  }
  
  public void pushNull()
  {
    mBackingList.add(null);
  }
  
  public void pushString(String paramString)
  {
    mBackingList.add(paramString);
  }
  
  public int size()
  {
    return mBackingList.size();
  }
  
  public ArrayList toArrayList()
  {
    return new ArrayList(mBackingList);
  }
  
  public String toString()
  {
    return mBackingList.toString();
  }
}

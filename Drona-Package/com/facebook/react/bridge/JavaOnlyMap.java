package com.facebook.react.bridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JavaOnlyMap
  implements ReadableMap, WritableMap
{
  private final Map mBackingMap;
  
  public JavaOnlyMap()
  {
    mBackingMap = new HashMap();
  }
  
  private JavaOnlyMap(Object... paramVarArgs)
  {
    if (paramVarArgs.length % 2 == 0)
    {
      mBackingMap = new HashMap();
      int i = 0;
      while (i < paramVarArgs.length)
      {
        Object localObject2 = paramVarArgs[(i + 1)];
        Object localObject1 = localObject2;
        if ((localObject2 instanceof Number)) {
          localObject1 = Double.valueOf(((Number)localObject2).doubleValue());
        }
        mBackingMap.put(paramVarArgs[i], localObject1);
        i += 2;
      }
      return;
    }
    throw new IllegalArgumentException("You must provide the same number of keys and values");
  }
  
  public static JavaOnlyMap deepClone(ReadableMap paramReadableMap)
  {
    JavaOnlyMap localJavaOnlyMap = new JavaOnlyMap();
    ReadableMapKeySetIterator localReadableMapKeySetIterator = paramReadableMap.keySetIterator();
    while (localReadableMapKeySetIterator.hasNextKey())
    {
      String str = localReadableMapKeySetIterator.nextKey();
      ReadableType localReadableType = paramReadableMap.getType(str);
      switch (2.$SwitchMap$com$facebook$react$bridge$ReadableType[localReadableType.ordinal()])
      {
      default: 
        break;
      case 6: 
        localJavaOnlyMap.putArray(str, JavaOnlyArray.deepClone(paramReadableMap.getArray(str)));
        break;
      case 5: 
        localJavaOnlyMap.putMap(str, deepClone(paramReadableMap.getMap(str)));
        break;
      case 4: 
        localJavaOnlyMap.putString(str, paramReadableMap.getString(str));
        break;
      case 3: 
        localJavaOnlyMap.putDouble(str, paramReadableMap.getDouble(str));
        break;
      case 2: 
        localJavaOnlyMap.putBoolean(str, paramReadableMap.getBoolean(str));
        break;
      case 1: 
        localJavaOnlyMap.putNull(str);
      }
    }
    return localJavaOnlyMap;
  }
  
  public static JavaOnlyMap of(Object... paramVarArgs)
  {
    return new JavaOnlyMap(paramVarArgs);
  }
  
  public WritableMap copy()
  {
    JavaOnlyMap localJavaOnlyMap = new JavaOnlyMap();
    localJavaOnlyMap.merge(this);
    return localJavaOnlyMap;
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
      paramObject = (JavaOnlyMap)paramObject;
      if (mBackingMap != null)
      {
        if (!mBackingMap.equals(mBackingMap)) {
          return false;
        }
      }
      else
      {
        if (mBackingMap == null) {
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
  
  public ReadableArray getArray(String paramString)
  {
    return (ReadableArray)mBackingMap.get(paramString);
  }
  
  public boolean getBoolean(String paramString)
  {
    return ((Boolean)mBackingMap.get(paramString)).booleanValue();
  }
  
  public double getDouble(String paramString)
  {
    return ((Number)mBackingMap.get(paramString)).doubleValue();
  }
  
  public Dynamic getDynamic(String paramString)
  {
    return DynamicFromMap.create(this, paramString);
  }
  
  public Iterator getEntryIterator()
  {
    return mBackingMap.entrySet().iterator();
  }
  
  public int getInt(String paramString)
  {
    return ((Number)mBackingMap.get(paramString)).intValue();
  }
  
  public ReadableMap getMap(String paramString)
  {
    return (ReadableMap)mBackingMap.get(paramString);
  }
  
  public String getString(String paramString)
  {
    return (String)mBackingMap.get(paramString);
  }
  
  public ReadableType getType(String paramString)
  {
    Object localObject = mBackingMap.get(paramString);
    if (localObject == null) {
      return ReadableType.Null;
    }
    if ((localObject instanceof Number)) {
      return ReadableType.Number;
    }
    if ((localObject instanceof String)) {
      return ReadableType.String;
    }
    if ((localObject instanceof Boolean)) {
      return ReadableType.Boolean;
    }
    if ((localObject instanceof ReadableMap)) {
      return ReadableType.GIF;
    }
    if ((localObject instanceof ReadableArray)) {
      return ReadableType.Array;
    }
    if ((localObject instanceof Dynamic)) {
      return ((Dynamic)localObject).getType();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Invalid value ");
    localStringBuilder.append(localObject.toString());
    localStringBuilder.append(" for key ");
    localStringBuilder.append(paramString);
    localStringBuilder.append("contained in JavaOnlyMap");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public boolean hasKey(String paramString)
  {
    return mBackingMap.containsKey(paramString);
  }
  
  public int hashCode()
  {
    if (mBackingMap != null) {
      return mBackingMap.hashCode();
    }
    return 0;
  }
  
  public boolean isNull(String paramString)
  {
    return mBackingMap.get(paramString) == null;
  }
  
  public ReadableMapKeySetIterator keySetIterator()
  {
    new ReadableMapKeySetIterator()
    {
      Iterator<Map.Entry<String, Object>> mIterator = mBackingMap.entrySet().iterator();
      
      public boolean hasNextKey()
      {
        return mIterator.hasNext();
      }
      
      public String nextKey()
      {
        return (String)((Map.Entry)mIterator.next()).getKey();
      }
    };
  }
  
  public void merge(ReadableMap paramReadableMap)
  {
    mBackingMap.putAll(mBackingMap);
  }
  
  public void putArray(String paramString, ReadableArray paramReadableArray)
  {
    mBackingMap.put(paramString, paramReadableArray);
  }
  
  public void putBoolean(String paramString, boolean paramBoolean)
  {
    mBackingMap.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  public void putDouble(String paramString, double paramDouble)
  {
    mBackingMap.put(paramString, Double.valueOf(paramDouble));
  }
  
  public void putInt(String paramString, int paramInt)
  {
    mBackingMap.put(paramString, new Double(paramInt));
  }
  
  public void putMap(String paramString, ReadableMap paramReadableMap)
  {
    mBackingMap.put(paramString, paramReadableMap);
  }
  
  public void putNull(String paramString)
  {
    mBackingMap.put(paramString, null);
  }
  
  public void putString(String paramString1, String paramString2)
  {
    mBackingMap.put(paramString1, paramString2);
  }
  
  public HashMap toHashMap()
  {
    return new HashMap(mBackingMap);
  }
  
  public String toString()
  {
    return mBackingMap.toString();
  }
}

package com.facebook.react.bridge;

import androidx.annotation.Nullable;
import com.facebook.infer.annotation.Assertions;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.upgrade.HybridData;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

@DoNotStrip
public class ReadableNativeMap
  extends NativeMap
  implements ReadableMap
{
  private static int mJniCallCounter;
  @Nullable
  private String[] mKeys;
  @Nullable
  private HashMap<String, Object> mLocalMap;
  @Nullable
  private HashMap<String, ReadableType> mLocalTypeMap;
  
  static {}
  
  protected ReadableNativeMap(HybridData paramHybridData)
  {
    super(paramHybridData);
  }
  
  private void checkInstance(String paramString, Object paramObject, Class paramClass)
  {
    if (paramObject != null)
    {
      if (paramClass.isInstance(paramObject)) {
        return;
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Value for ");
      localStringBuilder.append(paramString);
      localStringBuilder.append(" cannot be cast from ");
      localStringBuilder.append(paramObject.getClass().getSimpleName());
      localStringBuilder.append(" to ");
      localStringBuilder.append(paramClass.getSimpleName());
      throw new UnexpectedNativeTypeException(localStringBuilder.toString());
    }
  }
  
  public static int getJNIPassCounter()
  {
    return mJniCallCounter;
  }
  
  private HashMap getLocalMap()
  {
    if (mLocalMap != null) {
      return mLocalMap;
    }
    try
    {
      if (mKeys == null)
      {
        mKeys = ((String[])Assertions.assertNotNull(importKeys()));
        mJniCallCounter += 1;
      }
      if (mLocalMap == null)
      {
        Object[] arrayOfObject = (Object[])Assertions.assertNotNull(importValues());
        mJniCallCounter += 1;
        int j = mKeys.length;
        mLocalMap = new HashMap(j);
        int i = 0;
        while (i < j)
        {
          mLocalMap.put(mKeys[i], arrayOfObject[i]);
          i += 1;
        }
      }
      return mLocalMap;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private HashMap getLocalTypeMap()
  {
    if (mLocalTypeMap != null) {
      return mLocalTypeMap;
    }
    try
    {
      if (mKeys == null)
      {
        mKeys = ((String[])Assertions.assertNotNull(importKeys()));
        mJniCallCounter += 1;
      }
      if (mLocalTypeMap == null)
      {
        Object[] arrayOfObject = (Object[])Assertions.assertNotNull(importTypes());
        mJniCallCounter += 1;
        int j = mKeys.length;
        mLocalTypeMap = new HashMap(j);
        int i = 0;
        while (i < j)
        {
          mLocalTypeMap.put(mKeys[i], (ReadableType)arrayOfObject[i]);
          i += 1;
        }
      }
      return mLocalTypeMap;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  private Object getNullableValue(String paramString)
  {
    if (hasKey(paramString)) {
      return getLocalMap().get(paramString);
    }
    throw new NoSuchKeyException(paramString);
  }
  
  private Object getNullableValue(String paramString, Class paramClass)
  {
    Object localObject = getNullableValue(paramString);
    checkInstance(paramString, localObject, paramClass);
    return localObject;
  }
  
  private Object getValue(String paramString)
  {
    if ((hasKey(paramString)) && (!isNull(paramString))) {
      return Assertions.assertNotNull(getLocalMap().get(paramString));
    }
    throw new NoSuchKeyException(paramString);
  }
  
  private Object getValue(String paramString, Class paramClass)
  {
    Object localObject = getValue(paramString);
    checkInstance(paramString, localObject, paramClass);
    return localObject;
  }
  
  private native String[] importKeys();
  
  private native Object[] importTypes();
  
  private native Object[] importValues();
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof ReadableNativeMap)) {
      return false;
    }
    paramObject = (ReadableNativeMap)paramObject;
    return getLocalMap().equals(paramObject.getLocalMap());
  }
  
  public ReadableArray getArray(String paramString)
  {
    return (ReadableArray)getNullableValue(paramString, ReadableArray.class);
  }
  
  public boolean getBoolean(String paramString)
  {
    return ((Boolean)getValue(paramString, Boolean.class)).booleanValue();
  }
  
  public double getDouble(String paramString)
  {
    return ((Double)getValue(paramString, Double.class)).doubleValue();
  }
  
  public Dynamic getDynamic(String paramString)
  {
    return DynamicFromMap.create(this, paramString);
  }
  
  public Iterator getEntryIterator()
  {
    return getLocalMap().entrySet().iterator();
  }
  
  public int getInt(String paramString)
  {
    return ((Double)getValue(paramString, Double.class)).intValue();
  }
  
  public ReadableNativeMap getMap(String paramString)
  {
    return (ReadableNativeMap)getNullableValue(paramString, ReadableNativeMap.class);
  }
  
  public String getString(String paramString)
  {
    return (String)getNullableValue(paramString, String.class);
  }
  
  public ReadableType getType(String paramString)
  {
    if (getLocalTypeMap().containsKey(paramString)) {
      return (ReadableType)Assertions.assertNotNull(getLocalTypeMap().get(paramString));
    }
    throw new NoSuchKeyException(paramString);
  }
  
  public boolean hasKey(String paramString)
  {
    return getLocalMap().containsKey(paramString);
  }
  
  public int hashCode()
  {
    return getLocalMap().hashCode();
  }
  
  public boolean isNull(String paramString)
  {
    if (getLocalMap().containsKey(paramString)) {
      return getLocalMap().get(paramString) == null;
    }
    throw new NoSuchKeyException(paramString);
  }
  
  public ReadableMapKeySetIterator keySetIterator()
  {
    return new ReadableNativeMapKeySetIterator(this);
  }
  
  public HashMap toHashMap()
  {
    Object localObject = new HashMap(getLocalMap());
    Iterator localIterator = ((HashMap)localObject).keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      switch (1.$SwitchMap$com$facebook$react$bridge$ReadableType[getType(str).ordinal()])
      {
      case 1: 
      case 2: 
      case 3: 
      case 4: 
      default: 
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Could not convert object with key: ");
        ((StringBuilder)localObject).append(str);
        ((StringBuilder)localObject).append(".");
        throw new IllegalArgumentException(((StringBuilder)localObject).toString());
      case 6: 
        ((HashMap)localObject).put(str, ((ReadableArray)Assertions.assertNotNull(getArray(str))).toArrayList());
        break;
      case 5: 
        ((HashMap)localObject).put(str, ((ReadableNativeMap)Assertions.assertNotNull(getMap(str))).toHashMap());
      }
    }
    return localObject;
  }
  
  private static class ReadableNativeMapKeySetIterator
    implements ReadableMapKeySetIterator
  {
    private final Iterator<String> mIterator;
    
    public ReadableNativeMapKeySetIterator(ReadableNativeMap paramReadableNativeMap)
    {
      mIterator = paramReadableNativeMap.getLocalMap().keySet().iterator();
    }
    
    public boolean hasNextKey()
    {
      return mIterator.hasNext();
    }
    
    public String nextKey()
    {
      return (String)mIterator.next();
    }
  }
}

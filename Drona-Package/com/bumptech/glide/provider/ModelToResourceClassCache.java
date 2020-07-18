package com.bumptech.glide.provider;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import com.bumptech.glide.util.MultiClassKey;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ModelToResourceClassCache
{
  private final ArrayMap<MultiClassKey, List<Class<?>>> registeredResourceClassCache = new ArrayMap();
  private final AtomicReference<MultiClassKey> resourceClassKeyRef = new AtomicReference();
  
  public ModelToResourceClassCache() {}
  
  public void clear()
  {
    ArrayMap localArrayMap = registeredResourceClassCache;
    try
    {
      registeredResourceClassCache.clear();
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  public List get(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    MultiClassKey localMultiClassKey = (MultiClassKey)resourceClassKeyRef.getAndSet(null);
    if (localMultiClassKey == null)
    {
      paramClass1 = new MultiClassKey(paramClass1, paramClass2, paramClass3);
    }
    else
    {
      localMultiClassKey.set(paramClass1, paramClass2, paramClass3);
      paramClass1 = localMultiClassKey;
    }
    paramClass2 = registeredResourceClassCache;
    try
    {
      paramClass3 = (List)registeredResourceClassCache.get(paramClass1);
      resourceClassKeyRef.set(paramClass1);
      return paramClass3;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public void putAll(Class paramClass1, Class paramClass2, Class paramClass3, List paramList)
  {
    ArrayMap localArrayMap = registeredResourceClassCache;
    try
    {
      registeredResourceClassCache.put(new MultiClassKey(paramClass1, paramClass2, paramClass3), paramList);
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
}

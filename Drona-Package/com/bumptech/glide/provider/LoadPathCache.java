package com.bumptech.glide.provider;

import androidx.collection.ArrayMap;
import androidx.collection.SimpleArrayMap;
import com.bumptech.glide.load.engine.DecodePath;
import com.bumptech.glide.load.engine.LoadPath;
import com.bumptech.glide.load.resource.transcode.UnitTranscoder;
import com.bumptech.glide.util.MultiClassKey;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

public class LoadPathCache
{
  private static final LoadPath<?, ?, ?> NO_PATHS_SIGNAL = new LoadPath(Object.class, Object.class, Object.class, Collections.singletonList(new DecodePath(Object.class, Object.class, Object.class, Collections.emptyList(), new UnitTranscoder(), null)), null);
  private final ArrayMap<MultiClassKey, LoadPath<?, ?, ?>> cache = new ArrayMap();
  private final AtomicReference<MultiClassKey> keyRef = new AtomicReference();
  
  public LoadPathCache() {}
  
  private MultiClassKey getKey(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    MultiClassKey localMultiClassKey2 = (MultiClassKey)keyRef.getAndSet(null);
    MultiClassKey localMultiClassKey1 = localMultiClassKey2;
    if (localMultiClassKey2 == null) {
      localMultiClassKey1 = new MultiClassKey();
    }
    localMultiClassKey1.set(paramClass1, paramClass2, paramClass3);
    return localMultiClassKey1;
  }
  
  public boolean isEmptyLoadPath(LoadPath paramLoadPath)
  {
    return NO_PATHS_SIGNAL.equals(paramLoadPath);
  }
  
  public void putAll(Class paramClass1, Class paramClass2, Class paramClass3, LoadPath paramLoadPath)
  {
    ArrayMap localArrayMap1 = cache;
    try
    {
      ArrayMap localArrayMap2 = cache;
      paramClass1 = new MultiClassKey(paramClass1, paramClass2, paramClass3);
      if (paramLoadPath == null) {
        paramLoadPath = NO_PATHS_SIGNAL;
      }
      localArrayMap2.put(paramClass1, paramLoadPath);
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public LoadPath resolve(Class paramClass1, Class paramClass2, Class paramClass3)
  {
    paramClass2 = getKey(paramClass1, paramClass2, paramClass3);
    paramClass1 = cache;
    try
    {
      paramClass3 = (LoadPath)cache.get(paramClass2);
      keyRef.set(paramClass2);
      return paramClass3;
    }
    catch (Throwable paramClass2)
    {
      throw paramClass2;
    }
  }
}

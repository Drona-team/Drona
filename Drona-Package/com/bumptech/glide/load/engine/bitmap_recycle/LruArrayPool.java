package com.bumptech.glide.load.engine.bitmap_recycle;

import android.util.Log;
import androidx.annotation.VisibleForTesting;
import com.bumptech.glide.util.Preconditions;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

public final class LruArrayPool
  implements ArrayPool
{
  private static final int DEFAULT_SIZE = 4194304;
  @VisibleForTesting
  static final int MAX_OVER_SIZE_MULTIPLE = 8;
  private static final int SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2;
  private final Map<Class<?>, ArrayAdapterInterface<?>> adapters = new HashMap();
  private int currentSize;
  private final GroupedLinkedMap<Key, Object> groupedMap = new GroupedLinkedMap();
  private final KeyPool keyPool = new KeyPool();
  private final int maxSize;
  private final Map<Class<?>, NavigableMap<Integer, Integer>> sortedSizes = new HashMap();
  
  public LruArrayPool()
  {
    maxSize = 4194304;
  }
  
  public LruArrayPool(int paramInt)
  {
    maxSize = paramInt;
  }
  
  private void decrementArrayOfSize(int paramInt, Class paramClass)
  {
    paramClass = getSizesForAdapter(paramClass);
    Integer localInteger = (Integer)paramClass.get(Integer.valueOf(paramInt));
    if (localInteger != null)
    {
      if (localInteger.intValue() == 1)
      {
        paramClass.remove(Integer.valueOf(paramInt));
        return;
      }
      paramClass.put(Integer.valueOf(paramInt), Integer.valueOf(localInteger.intValue() - 1));
      return;
    }
    paramClass = new StringBuilder();
    paramClass.append("Tried to decrement empty size, size: ");
    paramClass.append(paramInt);
    paramClass.append(", this: ");
    paramClass.append(this);
    throw new NullPointerException(paramClass.toString());
  }
  
  private void evict()
  {
    evictToSize(maxSize);
  }
  
  private void evictToSize(int paramInt)
  {
    while (currentSize > paramInt)
    {
      Object localObject = groupedMap.removeLast();
      Preconditions.checkNotNull(localObject);
      ArrayAdapterInterface localArrayAdapterInterface = getAdapterFromObject(localObject);
      currentSize -= localArrayAdapterInterface.getArrayLength(localObject) * localArrayAdapterInterface.getElementSizeInBytes();
      decrementArrayOfSize(localArrayAdapterInterface.getArrayLength(localObject), localObject.getClass());
      if (Log.isLoggable(localArrayAdapterInterface.getTag(), 2))
      {
        String str = localArrayAdapterInterface.getTag();
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("evicted: ");
        localStringBuilder.append(localArrayAdapterInterface.getArrayLength(localObject));
        Log.v(str, localStringBuilder.toString());
      }
    }
  }
  
  private ArrayAdapterInterface getAdapterFromObject(Object paramObject)
  {
    return getAdapterFromType(paramObject.getClass());
  }
  
  private ArrayAdapterInterface getAdapterFromType(Class paramClass)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:552)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer$LiveA.onUseLocal(UnSSATransformer.java:1)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:166)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.onUse(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:331)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.travel(Cfg.java:387)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:90)\n\t... 17 more\n");
  }
  
  private Object getArrayForKey(Key paramKey)
  {
    return groupedMap.get(paramKey);
  }
  
  private Object getForKey(Key paramKey, Class paramClass)
  {
    ArrayAdapterInterface localArrayAdapterInterface = getAdapterFromType(paramClass);
    Object localObject = getArrayForKey(paramKey);
    if (localObject != null)
    {
      currentSize -= localArrayAdapterInterface.getArrayLength(localObject) * localArrayAdapterInterface.getElementSizeInBytes();
      decrementArrayOfSize(localArrayAdapterInterface.getArrayLength(localObject), paramClass);
    }
    paramClass = (Class)localObject;
    if (localObject == null)
    {
      if (Log.isLoggable(localArrayAdapterInterface.getTag(), 2))
      {
        paramClass = localArrayAdapterInterface.getTag();
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("Allocated ");
        ((StringBuilder)localObject).append(size);
        ((StringBuilder)localObject).append(" bytes");
        Log.v(paramClass, ((StringBuilder)localObject).toString());
      }
      paramClass = localArrayAdapterInterface.newArray(size);
    }
    return paramClass;
  }
  
  private NavigableMap getSizesForAdapter(Class paramClass)
  {
    Object localObject = (NavigableMap)sortedSizes.get(paramClass);
    if (localObject == null)
    {
      localObject = new TreeMap();
      sortedSizes.put(paramClass, localObject);
      return localObject;
    }
    return localObject;
  }
  
  private boolean isNoMoreThanHalfFull()
  {
    return (currentSize == 0) || (maxSize / currentSize >= 2);
  }
  
  private boolean isSmallEnoughForReuse(int paramInt)
  {
    return paramInt <= maxSize / 2;
  }
  
  private boolean mayFillRequest(int paramInt, Integer paramInteger)
  {
    return (paramInteger != null) && ((isNoMoreThanHalfFull()) || (paramInteger.intValue() <= paramInt * 8));
  }
  
  public void clearMemory()
  {
    try
    {
      evictToSize(0);
      return;
    }
    catch (Throwable localThrowable)
    {
      throw localThrowable;
    }
  }
  
  int getCurrentSize()
  {
    Object localObject1 = sortedSizes;
    Object localObject2 = this;
    Iterator localIterator = ((Map)localObject1).keySet().iterator();
    int j = 0;
    if (localIterator.hasNext())
    {
      Class localClass = (Class)localIterator.next();
      Object localObject3 = sortedSizes;
      localObject1 = localObject2;
      localObject3 = ((NavigableMap)((Map)localObject3).get(localClass)).keySet().iterator();
      int i = j;
      for (;;)
      {
        j = i;
        localObject2 = localObject1;
        if (!((Iterator)localObject3).hasNext()) {
          break;
        }
        localObject2 = (Integer)((Iterator)localObject3).next();
        ArrayAdapterInterface localArrayAdapterInterface = ((LruArrayPool)localObject1).getAdapterFromType(localClass);
        j = ((Integer)localObject2).intValue();
        Map localMap = sortedSizes;
        i += j * ((Integer)((NavigableMap)localMap.get(localClass)).get(localObject2)).intValue() * localArrayAdapterInterface.getElementSizeInBytes();
      }
    }
    return j;
  }
  
  public Object getExact(int paramInt, Class paramClass)
  {
    try
    {
      paramClass = getForKey(keyPool.get(paramInt, paramClass), paramClass);
      return paramClass;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  public void put(Object paramObject)
  {
    try
    {
      Object localObject2 = paramObject.getClass();
      Object localObject1 = getAdapterFromType((Class)localObject2);
      int i = ((ArrayAdapterInterface)localObject1).getArrayLength(paramObject);
      int j = ((ArrayAdapterInterface)localObject1).getElementSizeInBytes() * i;
      boolean bool = isSmallEnoughForReuse(j);
      if (!bool) {
        return;
      }
      localObject1 = keyPool.get(i, (Class)localObject2);
      groupedMap.put((Poolable)localObject1, paramObject);
      paramObject = getSizesForAdapter((Class)localObject2);
      localObject2 = (Integer)paramObject.get(Integer.valueOf(size));
      int k = size;
      i = 1;
      if (localObject2 != null) {
        i = 1 + ((Integer)localObject2).intValue();
      }
      paramObject.put(Integer.valueOf(k), Integer.valueOf(i));
      currentSize += j;
      evict();
      return;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public void put(Object paramObject, Class paramClass)
  {
    put(paramObject);
  }
  
  public void trimMemory(int paramInt)
  {
    if (paramInt >= 40) {}
    for (;;)
    {
      try
      {
        clearMemory();
        continue;
        evictToSize(maxSize / 2);
        return;
      }
      catch (Throwable localThrowable)
      {
        Object localObject;
        continue;
      }
      throw localObject;
      if (paramInt < 20) {
        if (paramInt != 15) {}
      }
    }
  }
  
  public Object w(int paramInt, Class paramClass)
  {
    try
    {
      Object localObject = (Integer)getSizesForAdapter(paramClass).ceilingKey(Integer.valueOf(paramInt));
      if (mayFillRequest(paramInt, (Integer)localObject)) {
        localObject = keyPool.get(((Integer)localObject).intValue(), paramClass);
      } else {
        localObject = keyPool.get(paramInt, paramClass);
      }
      paramClass = getForKey((Key)localObject, paramClass);
      return paramClass;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  private static final class Key
    implements Poolable
  {
    private Class<?> arrayClass;
    private final LruArrayPool.KeyPool pool;
    int size;
    
    Key(LruArrayPool.KeyPool paramKeyPool)
    {
      pool = paramKeyPool;
    }
    
    public boolean equals(Object paramObject)
    {
      if ((paramObject instanceof Key))
      {
        paramObject = (Key)paramObject;
        if ((size == size) && (arrayClass == arrayClass)) {
          return true;
        }
      }
      return false;
    }
    
    public int hashCode()
    {
      int j = size;
      int i;
      if (arrayClass != null) {
        i = arrayClass.hashCode();
      } else {
        i = 0;
      }
      return j * 31 + i;
    }
    
    void init(int paramInt, Class paramClass)
    {
      size = paramInt;
      arrayClass = paramClass;
    }
    
    public void offer()
    {
      pool.offer(this);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Key{size=");
      localStringBuilder.append(size);
      localStringBuilder.append("array=");
      localStringBuilder.append(arrayClass);
      localStringBuilder.append('}');
      return localStringBuilder.toString();
    }
  }
  
  private static final class KeyPool
    extends BaseKeyPool<LruArrayPool.Key>
  {
    KeyPool() {}
    
    protected LruArrayPool.Key create()
    {
      return new LruArrayPool.Key(this);
    }
    
    LruArrayPool.Key get(int paramInt, Class paramClass)
    {
      LruArrayPool.Key localKey = (LruArrayPool.Key)get();
      localKey.init(paramInt, paramClass);
      return localKey;
    }
  }
}

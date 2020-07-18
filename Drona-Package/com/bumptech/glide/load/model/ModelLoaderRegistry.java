package com.bumptech.glide.load.model;

import androidx.core.util.Pools.Pool;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModelLoaderRegistry
{
  private final ModelLoaderCache cache = new ModelLoaderCache();
  private final MultiModelLoaderFactory multiModelLoaderFactory;
  
  public ModelLoaderRegistry(Pools.Pool paramPool)
  {
    this(new MultiModelLoaderFactory(paramPool));
  }
  
  private ModelLoaderRegistry(MultiModelLoaderFactory paramMultiModelLoaderFactory)
  {
    multiModelLoaderFactory = paramMultiModelLoaderFactory;
  }
  
  private static Class getClass(Object paramObject)
  {
    return paramObject.getClass();
  }
  
  private List getModelLoadersForClass(Class paramClass)
  {
    try
    {
      List localList2 = cache.load(paramClass);
      List localList1 = localList2;
      if (localList2 == null)
      {
        localList2 = Collections.unmodifiableList(multiModelLoaderFactory.build(paramClass));
        localList1 = localList2;
        cache.sort(paramClass, localList2);
      }
      return localList1;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  private void tearDown(List paramList)
  {
    paramList = paramList.iterator();
    while (paramList.hasNext()) {
      ((ModelLoaderFactory)paramList.next()).teardown();
    }
  }
  
  public void append(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    try
    {
      multiModelLoaderFactory.append(paramClass1, paramClass2, paramModelLoaderFactory);
      cache.clear();
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public ModelLoader build(Class paramClass1, Class paramClass2)
  {
    try
    {
      paramClass1 = multiModelLoaderFactory.build(paramClass1, paramClass2);
      return paramClass1;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public List getDataClasses(Class paramClass)
  {
    try
    {
      paramClass = multiModelLoaderFactory.getDataClasses(paramClass);
      return paramClass;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  public List getModelLoaders(Object paramObject)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a12 = a11\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void prepend(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    try
    {
      multiModelLoaderFactory.prepend(paramClass1, paramClass2, paramModelLoaderFactory);
      cache.clear();
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public void remove(Class paramClass1, Class paramClass2)
  {
    try
    {
      tearDown(multiModelLoaderFactory.remove(paramClass1, paramClass2));
      cache.clear();
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public void replace(Class paramClass1, Class paramClass2, ModelLoaderFactory paramModelLoaderFactory)
  {
    try
    {
      tearDown(multiModelLoaderFactory.replace(paramClass1, paramClass2, paramModelLoaderFactory));
      cache.clear();
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  private static class ModelLoaderCache
  {
    private final Map<Class<?>, Entry<?>> cachedModelLoaders = new HashMap();
    
    ModelLoaderCache() {}
    
    public void clear()
    {
      cachedModelLoaders.clear();
    }
    
    public List load(Class paramClass)
    {
      paramClass = (Entry)cachedModelLoaders.get(paramClass);
      if (paramClass == null) {
        return null;
      }
      return loaders;
    }
    
    public void sort(Class paramClass, List paramList)
    {
      if ((Entry)cachedModelLoaders.put(paramClass, new Entry(paramList)) == null) {
        return;
      }
      paramList = new StringBuilder();
      paramList.append("Already cached loaders for model: ");
      paramList.append(paramClass);
      throw new IllegalStateException(paramList.toString());
    }
    
    private static class Entry<Model>
    {
      final List<ModelLoader<Model, ?>> loaders;
      
      public Entry(List paramList)
      {
        loaders = paramList;
      }
    }
  }
}

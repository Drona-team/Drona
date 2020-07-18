package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ResourceDecoderRegistry
{
  private final List<String> bucketPriorityList = new ArrayList();
  private final Map<String, List<Entry<?, ?>>> decoders = new HashMap();
  
  public ResourceDecoderRegistry() {}
  
  private List getOrAddEntryList(String paramString)
  {
    throw new Runtime("d2j fail translate: java.lang.RuntimeException: fail exe a4 = a3\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:92)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.exec(BaseAnalyze.java:1)\n\tat com.googlecode.dex2jar.ir.ts.Cfg.dfs(Cfg.java:255)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze0(BaseAnalyze.java:75)\n\tat com.googlecode.dex2jar.ir.ts.an.BaseAnalyze.analyze(BaseAnalyze.java:69)\n\tat com.googlecode.dex2jar.ir.ts.UnSSATransformer.transform(UnSSATransformer.java:274)\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:163)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\nCaused by: java.lang.NullPointerException\n");
  }
  
  public void append(String paramString, ResourceDecoder paramResourceDecoder, Class paramClass1, Class paramClass2)
  {
    try
    {
      getOrAddEntryList(paramString).add(new Entry(paramClass1, paramClass2, paramResourceDecoder));
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public List getDecoders(Class paramClass1, Class paramClass2)
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = bucketPriorityList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (String)localIterator.next();
        localObject = (List)decoders.get(localObject);
        if (localObject != null)
        {
          localObject = ((List)localObject).iterator();
          while (((Iterator)localObject).hasNext())
          {
            Entry localEntry = (Entry)((Iterator)localObject).next();
            if (localEntry.handles(paramClass1, paramClass2)) {
              localArrayList.add(decoder);
            }
          }
        }
      }
      return localArrayList;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public List getResourceClasses(Class paramClass1, Class paramClass2)
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      Iterator localIterator = bucketPriorityList.iterator();
      while (localIterator.hasNext())
      {
        Object localObject = (String)localIterator.next();
        localObject = (List)decoders.get(localObject);
        if (localObject != null)
        {
          localObject = ((List)localObject).iterator();
          while (((Iterator)localObject).hasNext())
          {
            Entry localEntry = (Entry)((Iterator)localObject).next();
            if ((localEntry.handles(paramClass1, paramClass2)) && (!localArrayList.contains(resourceClass))) {
              localArrayList.add(resourceClass);
            }
          }
        }
      }
      return localArrayList;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public void prepend(String paramString, ResourceDecoder paramResourceDecoder, Class paramClass1, Class paramClass2)
  {
    try
    {
      getOrAddEntryList(paramString).add(0, new Entry(paramClass1, paramClass2, paramResourceDecoder));
      return;
    }
    catch (Throwable paramString)
    {
      throw paramString;
    }
  }
  
  public void setBucketPriorityList(List paramList)
  {
    try
    {
      Object localObject = new ArrayList(bucketPriorityList);
      bucketPriorityList.clear();
      bucketPriorityList.addAll(paramList);
      localObject = ((List)localObject).iterator();
      while (((Iterator)localObject).hasNext())
      {
        String str = (String)((Iterator)localObject).next();
        if (!paramList.contains(str)) {
          bucketPriorityList.add(str);
        }
      }
      return;
    }
    catch (Throwable paramList)
    {
      throw paramList;
    }
  }
  
  private static class Entry<T, R>
  {
    private final Class<T> dataClass;
    final ResourceDecoder<T, R> decoder;
    final Class<R> resourceClass;
    
    public Entry(Class paramClass1, Class paramClass2, ResourceDecoder paramResourceDecoder)
    {
      dataClass = paramClass1;
      resourceClass = paramClass2;
      decoder = paramResourceDecoder;
    }
    
    public boolean handles(Class paramClass1, Class paramClass2)
    {
      return (dataClass.isAssignableFrom(paramClass1)) && (paramClass2.isAssignableFrom(resourceClass));
    }
  }
}

package com.bumptech.glide.load.resource.transcode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TranscoderRegistry
{
  private final List<Entry<?, ?>> transcoders = new ArrayList();
  
  public TranscoderRegistry() {}
  
  public ResourceTranscoder get(Class paramClass1, Class paramClass2)
  {
    try
    {
      if (paramClass2.isAssignableFrom(paramClass1))
      {
        paramClass1 = UnitTranscoder.get();
        return paramClass1;
      }
      Object localObject = transcoders.iterator();
      while (((Iterator)localObject).hasNext())
      {
        Entry localEntry = (Entry)((Iterator)localObject).next();
        if (localEntry.handles(paramClass1, paramClass2))
        {
          paramClass1 = transcoder;
          return paramClass1;
        }
      }
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("No transcoder registered to transcode from ");
      ((StringBuilder)localObject).append(paramClass1);
      ((StringBuilder)localObject).append(" to ");
      ((StringBuilder)localObject).append(paramClass2);
      throw new IllegalArgumentException(((StringBuilder)localObject).toString());
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public List getTranscodeClasses(Class paramClass1, Class paramClass2)
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      if (paramClass2.isAssignableFrom(paramClass1))
      {
        localArrayList.add(paramClass2);
        return localArrayList;
      }
      Iterator localIterator = transcoders.iterator();
      while (localIterator.hasNext()) {
        if (((Entry)localIterator.next()).handles(paramClass1, paramClass2)) {
          localArrayList.add(paramClass2);
        }
      }
      return localArrayList;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  public void register(Class paramClass1, Class paramClass2, ResourceTranscoder paramResourceTranscoder)
  {
    try
    {
      transcoders.add(new Entry(paramClass1, paramClass2, paramResourceTranscoder));
      return;
    }
    catch (Throwable paramClass1)
    {
      throw paramClass1;
    }
  }
  
  private static final class Entry<Z, R>
  {
    private final Class<Z> fromClass;
    private final Class<R> toClass;
    final ResourceTranscoder<Z, R> transcoder;
    
    Entry(Class paramClass1, Class paramClass2, ResourceTranscoder paramResourceTranscoder)
    {
      fromClass = paramClass1;
      toClass = paramClass2;
      transcoder = paramResourceTranscoder;
    }
    
    public boolean handles(Class paramClass1, Class paramClass2)
    {
      return (fromClass.isAssignableFrom(paramClass1)) && (paramClass2.isAssignableFrom(toClass));
    }
  }
}

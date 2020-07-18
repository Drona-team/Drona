package com.bumptech.glide.provider;

import com.bumptech.glide.load.Encoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EncoderRegistry
{
  private final List<Entry<?>> encoders = new ArrayList();
  
  public EncoderRegistry() {}
  
  public void append(Class paramClass, Encoder paramEncoder)
  {
    try
    {
      encoders.add(new Entry(paramClass, paramEncoder));
      return;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  public Encoder getEncoder(Class paramClass)
  {
    try
    {
      Iterator localIterator = encoders.iterator();
      while (localIterator.hasNext())
      {
        Entry localEntry = (Entry)localIterator.next();
        if (localEntry.handles(paramClass))
        {
          paramClass = encoder;
          return paramClass;
        }
      }
      return null;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  public void prepend(Class paramClass, Encoder paramEncoder)
  {
    try
    {
      encoders.add(0, new Entry(paramClass, paramEncoder));
      return;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  private static final class Entry<T>
  {
    private final Class<T> dataClass;
    final Encoder<T> encoder;
    
    Entry(Class paramClass, Encoder paramEncoder)
    {
      dataClass = paramClass;
      encoder = paramEncoder;
    }
    
    boolean handles(Class paramClass)
    {
      return dataClass.isAssignableFrom(paramClass);
    }
  }
}

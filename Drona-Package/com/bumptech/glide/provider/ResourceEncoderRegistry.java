package com.bumptech.glide.provider;

import com.bumptech.glide.load.ResourceEncoder;
import java.util.ArrayList;
import java.util.List;

public class ResourceEncoderRegistry
{
  private final List<Entry<?>> encoders = new ArrayList();
  
  public ResourceEncoderRegistry() {}
  
  public void append(Class paramClass, ResourceEncoder paramResourceEncoder)
  {
    try
    {
      encoders.add(new Entry(paramClass, paramResourceEncoder));
      return;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  public ResourceEncoder decode(Class paramClass)
  {
    int i = 0;
    try
    {
      int j = encoders.size();
      while (i < j)
      {
        Entry localEntry = (Entry)encoders.get(i);
        if (localEntry.handles(paramClass))
        {
          paramClass = encoder;
          return paramClass;
        }
        i += 1;
      }
      return null;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  public void prepend(Class paramClass, ResourceEncoder paramResourceEncoder)
  {
    try
    {
      encoders.add(0, new Entry(paramClass, paramResourceEncoder));
      return;
    }
    catch (Throwable paramClass)
    {
      throw paramClass;
    }
  }
  
  private static final class Entry<T>
  {
    final ResourceEncoder<T> encoder;
    private final Class<T> resourceClass;
    
    Entry(Class paramClass, ResourceEncoder paramResourceEncoder)
    {
      resourceClass = paramClass;
      encoder = paramResourceEncoder;
    }
    
    boolean handles(Class paramClass)
    {
      return resourceClass.isAssignableFrom(paramClass);
    }
  }
}

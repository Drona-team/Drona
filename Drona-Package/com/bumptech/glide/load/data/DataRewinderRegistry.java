package com.bumptech.glide.load.data;

import com.bumptech.glide.util.Preconditions;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DataRewinderRegistry
{
  private static final DataRewinder.Factory<?> DEFAULT_FACTORY = new DataRewinder.Factory()
  {
    public DataRewinder build(Object paramAnonymousObject)
    {
      return new DataRewinderRegistry.DefaultRewinder(paramAnonymousObject);
    }
    
    public Class getDataClass()
    {
      throw new UnsupportedOperationException("Not implemented");
    }
  };
  private final Map<Class<?>, DataRewinder.Factory<?>> rewinders = new HashMap();
  
  public DataRewinderRegistry() {}
  
  public DataRewinder build(Object paramObject)
  {
    try
    {
      Preconditions.checkNotNull(paramObject);
      Object localObject2 = (DataRewinder.Factory)rewinders.get(paramObject.getClass());
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        Iterator localIterator = rewinders.values().iterator();
        do
        {
          localObject1 = localObject2;
          if (!localIterator.hasNext()) {
            break;
          }
          localObject1 = (DataRewinder.Factory)localIterator.next();
        } while (!((DataRewinder.Factory)localObject1).getDataClass().isAssignableFrom(paramObject.getClass()));
      }
      localObject2 = localObject1;
      if (localObject1 == null) {
        localObject2 = DEFAULT_FACTORY;
      }
      paramObject = ((DataRewinder.Factory)localObject2).build(paramObject);
      return paramObject;
    }
    catch (Throwable paramObject)
    {
      throw paramObject;
    }
  }
  
  public void register(DataRewinder.Factory paramFactory)
  {
    try
    {
      rewinders.put(paramFactory.getDataClass(), paramFactory);
      return;
    }
    catch (Throwable paramFactory)
    {
      throw paramFactory;
    }
  }
  
  private static final class DefaultRewinder
    implements DataRewinder<Object>
  {
    private final Object data;
    
    DefaultRewinder(Object paramObject)
    {
      data = paramObject;
    }
    
    public void cleanup() {}
    
    public Object rewindAndGet()
    {
      return data;
    }
  }
}

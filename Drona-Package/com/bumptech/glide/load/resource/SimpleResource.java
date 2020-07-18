package com.bumptech.glide.load.resource;

import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.util.Preconditions;

public class SimpleResource<T>
  implements Resource<T>
{
  protected final T data;
  
  public SimpleResource(Object paramObject)
  {
    data = Preconditions.checkNotNull(paramObject);
  }
  
  public final Object get()
  {
    return data;
  }
  
  public Class getResourceClass()
  {
    return data.getClass();
  }
  
  public final int getSize()
  {
    return 1;
  }
  
  public void recycle() {}
}

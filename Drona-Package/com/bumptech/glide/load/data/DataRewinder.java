package com.bumptech.glide.load.data;

import java.io.IOException;

public abstract interface DataRewinder<T>
{
  public abstract void cleanup();
  
  public abstract Object rewindAndGet()
    throws IOException;
  
  public static abstract interface Factory<T>
  {
    public abstract DataRewinder build(Object paramObject);
    
    public abstract Class getDataClass();
  }
}

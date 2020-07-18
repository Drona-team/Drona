package com.bumptech.glide.load.engine.bitmap_recycle;

public abstract interface ArrayPool
{
  public static final int STANDARD_BUFFER_SIZE_BYTES = 65536;
  
  public abstract void clearMemory();
  
  public abstract Object getExact(int paramInt, Class paramClass);
  
  public abstract void put(Object paramObject);
  
  public abstract void put(Object paramObject, Class paramClass);
  
  public abstract void trimMemory(int paramInt);
  
  public abstract Object w(int paramInt, Class paramClass);
}

package com.bumptech.glide.load.engine.bitmap_recycle;

abstract interface ArrayAdapterInterface<T>
{
  public abstract int getArrayLength(Object paramObject);
  
  public abstract int getElementSizeInBytes();
  
  public abstract String getTag();
  
  public abstract Object newArray(int paramInt);
}

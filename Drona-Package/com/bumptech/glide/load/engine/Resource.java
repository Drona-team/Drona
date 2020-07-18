package com.bumptech.glide.load.engine;

public abstract interface Resource<Z>
{
  public abstract Object get();
  
  public abstract Class getResourceClass();
  
  public abstract int getSize();
  
  public abstract void recycle();
}

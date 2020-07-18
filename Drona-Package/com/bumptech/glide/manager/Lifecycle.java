package com.bumptech.glide.manager;

public abstract interface Lifecycle
{
  public abstract void addListener(LifecycleListener paramLifecycleListener);
  
  public abstract void removeListener(LifecycleListener paramLifecycleListener);
}

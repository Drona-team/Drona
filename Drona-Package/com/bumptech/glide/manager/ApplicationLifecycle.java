package com.bumptech.glide.manager;

class ApplicationLifecycle
  implements Lifecycle
{
  ApplicationLifecycle() {}
  
  public void addListener(LifecycleListener paramLifecycleListener)
  {
    paramLifecycleListener.onStart();
  }
  
  public void removeListener(LifecycleListener paramLifecycleListener) {}
}

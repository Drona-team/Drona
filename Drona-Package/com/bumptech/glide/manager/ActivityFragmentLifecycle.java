package com.bumptech.glide.manager;

import com.bumptech.glide.util.Util;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

class ActivityFragmentLifecycle
  implements Lifecycle
{
  private boolean isDestroyed;
  private boolean isStarted;
  private final Set<LifecycleListener> lifecycleListeners = Collections.newSetFromMap(new WeakHashMap());
  
  ActivityFragmentLifecycle() {}
  
  public void addListener(LifecycleListener paramLifecycleListener)
  {
    lifecycleListeners.add(paramLifecycleListener);
    if (isDestroyed)
    {
      paramLifecycleListener.onDestroy();
      return;
    }
    if (isStarted)
    {
      paramLifecycleListener.onStart();
      return;
    }
    paramLifecycleListener.onStop();
  }
  
  void onDestroy()
  {
    isDestroyed = true;
    Iterator localIterator = Util.getSnapshot(lifecycleListeners).iterator();
    while (localIterator.hasNext()) {
      ((LifecycleListener)localIterator.next()).onDestroy();
    }
  }
  
  void onStart()
  {
    isStarted = true;
    Iterator localIterator = Util.getSnapshot(lifecycleListeners).iterator();
    while (localIterator.hasNext()) {
      ((LifecycleListener)localIterator.next()).onStart();
    }
  }
  
  void onStop()
  {
    isStarted = false;
    Iterator localIterator = Util.getSnapshot(lifecycleListeners).iterator();
    while (localIterator.hasNext()) {
      ((LifecycleListener)localIterator.next()).onStop();
    }
  }
  
  public void removeListener(LifecycleListener paramLifecycleListener)
  {
    lifecycleListeners.remove(paramLifecycleListener);
  }
}

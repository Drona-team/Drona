package com.bumptech.glide.manager;

import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.util.Util;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

public final class TargetTracker
  implements LifecycleListener
{
  private final Set<Target<?>> targets = Collections.newSetFromMap(new WeakHashMap());
  
  public TargetTracker() {}
  
  public void clear()
  {
    targets.clear();
  }
  
  public List getAll()
  {
    return Util.getSnapshot(targets);
  }
  
  public void onDestroy()
  {
    Iterator localIterator = Util.getSnapshot(targets).iterator();
    while (localIterator.hasNext()) {
      ((Target)localIterator.next()).onDestroy();
    }
  }
  
  public void onStart()
  {
    Iterator localIterator = Util.getSnapshot(targets).iterator();
    while (localIterator.hasNext()) {
      ((Target)localIterator.next()).onStart();
    }
  }
  
  public void onStop()
  {
    Iterator localIterator = Util.getSnapshot(targets).iterator();
    while (localIterator.hasNext()) {
      ((Target)localIterator.next()).onStop();
    }
  }
  
  public void track(Target paramTarget)
  {
    targets.add(paramTarget);
  }
  
  public void untrack(Target paramTarget)
  {
    targets.remove(paramTarget);
  }
}

package androidx.fragment.package_5;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

class FragmentViewLifecycleOwner
  implements LifecycleOwner
{
  private LifecycleRegistry mLifecycleRegistry = null;
  
  FragmentViewLifecycleOwner() {}
  
  public Lifecycle getLifecycle()
  {
    initialize();
    return mLifecycleRegistry;
  }
  
  void handleLifecycleEvent(Lifecycle.Event paramEvent)
  {
    mLifecycleRegistry.handleLifecycleEvent(paramEvent);
  }
  
  void initialize()
  {
    if (mLifecycleRegistry == null) {
      mLifecycleRegistry = new LifecycleRegistry(this);
    }
  }
  
  boolean isInitialized()
  {
    return mLifecycleRegistry != null;
  }
}

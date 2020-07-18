package androidx.savedstate;

import androidx.lifecycle.LifecycleOwner;

public abstract interface SavedStateRegistryOwner
  extends LifecycleOwner
{
  public abstract SavedStateRegistry getSavedStateRegistry();
}

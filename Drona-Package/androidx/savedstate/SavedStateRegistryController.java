package androidx.savedstate;

import android.os.Bundle;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;

public final class SavedStateRegistryController
{
  private final SavedStateRegistryOwner mOwner;
  private final SavedStateRegistry mRegistry;
  
  private SavedStateRegistryController(SavedStateRegistryOwner paramSavedStateRegistryOwner)
  {
    mOwner = paramSavedStateRegistryOwner;
    mRegistry = new SavedStateRegistry();
  }
  
  public static SavedStateRegistryController create(SavedStateRegistryOwner paramSavedStateRegistryOwner)
  {
    return new SavedStateRegistryController(paramSavedStateRegistryOwner);
  }
  
  public SavedStateRegistry getSavedStateRegistry()
  {
    return mRegistry;
  }
  
  public void performRestore(Bundle paramBundle)
  {
    Lifecycle localLifecycle = mOwner.getLifecycle();
    if (localLifecycle.getCurrentState() == Lifecycle.State.INITIALIZED)
    {
      localLifecycle.addObserver(new Recreator(mOwner));
      mRegistry.performRestore(localLifecycle, paramBundle);
      return;
    }
    throw new IllegalStateException("Restarter must be created only during owner's initialization stage");
  }
  
  public void performSave(Bundle paramBundle)
  {
    mRegistry.performSave(paramBundle);
  }
}

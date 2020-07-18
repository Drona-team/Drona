package androidx.lifecycle;

import android.os.Bundle;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistry.AutoRecreated;
import androidx.savedstate.SavedStateRegistryOwner;
import java.util.Iterator;
import java.util.Set;

final class SavedStateHandleController
  implements LifecycleEventObserver
{
  static final String TAG_SAVED_STATE_HANDLE_CONTROLLER = "androidx.lifecycle.savedstate.vm.tag";
  private final SavedStateHandle mHandle;
  private boolean mIsAttached = false;
  private final String mKey;
  
  SavedStateHandleController(String paramString, SavedStateHandle paramSavedStateHandle)
  {
    mKey = paramString;
    mHandle = paramSavedStateHandle;
  }
  
  static void attachHandleIfNeeded(ViewModel paramViewModel, SavedStateRegistry paramSavedStateRegistry, Lifecycle paramLifecycle)
  {
    paramViewModel = (SavedStateHandleController)paramViewModel.getTag("androidx.lifecycle.savedstate.vm.tag");
    if ((paramViewModel != null) && (!paramViewModel.isAttached()))
    {
      paramViewModel.attachToLifecycle(paramSavedStateRegistry, paramLifecycle);
      tryToAddRecreator(paramSavedStateRegistry, paramLifecycle);
    }
  }
  
  static SavedStateHandleController create(SavedStateRegistry paramSavedStateRegistry, Lifecycle paramLifecycle, String paramString, Bundle paramBundle)
  {
    paramString = new SavedStateHandleController(paramString, SavedStateHandle.createHandle(paramSavedStateRegistry.consumeRestoredStateForKey(paramString), paramBundle));
    paramString.attachToLifecycle(paramSavedStateRegistry, paramLifecycle);
    tryToAddRecreator(paramSavedStateRegistry, paramLifecycle);
    return paramString;
  }
  
  private static void tryToAddRecreator(final SavedStateRegistry paramSavedStateRegistry, Lifecycle paramLifecycle)
  {
    Lifecycle.State localState = paramLifecycle.getCurrentState();
    if ((localState != Lifecycle.State.INITIALIZED) && (!localState.isAtLeast(Lifecycle.State.STARTED)))
    {
      paramLifecycle.addObserver(new LifecycleEventObserver()
      {
        public void onStateChanged(LifecycleOwner paramAnonymousLifecycleOwner, Lifecycle.Event paramAnonymousEvent)
        {
          if (paramAnonymousEvent == Lifecycle.Event.ON_START)
          {
            val$lifecycle.removeObserver(this);
            paramSavedStateRegistry.runOnNextRecreation(SavedStateHandleController.OnRecreation.class);
          }
        }
      });
      return;
    }
    paramSavedStateRegistry.runOnNextRecreation(OnRecreation.class);
  }
  
  void attachToLifecycle(SavedStateRegistry paramSavedStateRegistry, Lifecycle paramLifecycle)
  {
    if (!mIsAttached)
    {
      mIsAttached = true;
      paramLifecycle.addObserver(this);
      paramSavedStateRegistry.registerSavedStateProvider(mKey, mHandle.savedStateProvider());
      return;
    }
    throw new IllegalStateException("Already attached to lifecycleOwner");
  }
  
  SavedStateHandle getHandle()
  {
    return mHandle;
  }
  
  boolean isAttached()
  {
    return mIsAttached;
  }
  
  public void onStateChanged(LifecycleOwner paramLifecycleOwner, Lifecycle.Event paramEvent)
  {
    if (paramEvent == Lifecycle.Event.ON_DESTROY)
    {
      mIsAttached = false;
      paramLifecycleOwner.getLifecycle().removeObserver(this);
    }
  }
  
  static final class OnRecreation
    implements SavedStateRegistry.AutoRecreated
  {
    OnRecreation() {}
    
    public void onRecreated(SavedStateRegistryOwner paramSavedStateRegistryOwner)
    {
      if ((paramSavedStateRegistryOwner instanceof ViewModelStoreOwner))
      {
        ViewModelStore localViewModelStore = ((ViewModelStoreOwner)paramSavedStateRegistryOwner).getViewModelStore();
        SavedStateRegistry localSavedStateRegistry = paramSavedStateRegistryOwner.getSavedStateRegistry();
        Iterator localIterator = localViewModelStore.keys().iterator();
        while (localIterator.hasNext()) {
          SavedStateHandleController.attachHandleIfNeeded(localViewModelStore.getAll((String)localIterator.next()), localSavedStateRegistry, paramSavedStateRegistryOwner.getLifecycle());
        }
        if (!localViewModelStore.keys().isEmpty()) {
          localSavedStateRegistry.runOnNextRecreation(OnRecreation.class);
        }
      }
      else
      {
        throw new IllegalStateException("Internal error: OnRecreation should be registered only on componentsthat implement ViewModelStoreOwner");
      }
    }
  }
}

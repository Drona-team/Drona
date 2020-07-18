package androidx.lifecycle;

import android.os.Bundle;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryOwner;

public abstract class AbstractSavedStateViewModelFactory
  extends ViewModelProvider.KeyedFactory
{
  static final String TAG_SAVED_STATE_HANDLE_CONTROLLER = "androidx.lifecycle.savedstate.vm.tag";
  private final Bundle mDefaultArgs;
  private final Lifecycle mLifecycle;
  private final SavedStateRegistry mSavedStateRegistry;
  
  public AbstractSavedStateViewModelFactory(SavedStateRegistryOwner paramSavedStateRegistryOwner, Bundle paramBundle)
  {
    mSavedStateRegistry = paramSavedStateRegistryOwner.getSavedStateRegistry();
    mLifecycle = paramSavedStateRegistryOwner.getLifecycle();
    mDefaultArgs = paramBundle;
  }
  
  public final ViewModel create(Class paramClass)
  {
    String str = paramClass.getCanonicalName();
    if (str != null) {
      return create(str, paramClass);
    }
    throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
  }
  
  public final ViewModel create(String paramString, Class paramClass)
  {
    SavedStateHandleController localSavedStateHandleController = SavedStateHandleController.create(mSavedStateRegistry, mLifecycle, paramString, mDefaultArgs);
    paramString = create(paramString, paramClass, localSavedStateHandleController.getHandle());
    paramString.setTagIfAbsent("androidx.lifecycle.savedstate.vm.tag", localSavedStateHandleController);
    return paramString;
  }
  
  protected abstract ViewModel create(String paramString, Class paramClass, SavedStateHandle paramSavedStateHandle);
  
  void onRequery(ViewModel paramViewModel)
  {
    SavedStateHandleController.attachHandleIfNeeded(paramViewModel, mSavedStateRegistry, mLifecycle);
  }
}

package androidx.savedstate;

import android.annotation.SuppressLint;
import android.os.BaseBundle;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.arch.core.internal.SafeIterableMap.IteratorWithAdditions;
import androidx.lifecycle.GenericLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleOwner;
import java.util.Iterator;
import java.util.Map.Entry;

@SuppressLint({"RestrictedApi"})
public final class SavedStateRegistry
{
  private static final String SAVED_COMPONENTS_KEY = "androidx.lifecycle.BundlableSavedStateRegistry.key";
  boolean mAllowingSavingState = true;
  private SafeIterableMap<String, SavedStateProvider> mComponents = new SafeIterableMap();
  private Recreator.SavedStateProvider mRecreatorProvider;
  private boolean mRestored;
  @Nullable
  private Bundle mRestoredState;
  
  SavedStateRegistry() {}
  
  public Bundle consumeRestoredStateForKey(String paramString)
  {
    Bundle localBundle;
    if (mRestored)
    {
      if (mRestoredState != null)
      {
        localBundle = mRestoredState.getBundle(paramString);
        mRestoredState.remove(paramString);
        if (mRestoredState.isEmpty())
        {
          mRestoredState = null;
          return localBundle;
        }
      }
      else
      {
        return null;
      }
    }
    else {
      throw new IllegalStateException("You can consumeRestoredStateForKey only after super.onCreate of corresponding component");
    }
    return localBundle;
  }
  
  public boolean isRestored()
  {
    return mRestored;
  }
  
  void performRestore(Lifecycle paramLifecycle, Bundle paramBundle)
  {
    if (!mRestored)
    {
      if (paramBundle != null) {
        mRestoredState = paramBundle.getBundle("androidx.lifecycle.BundlableSavedStateRegistry.key");
      }
      paramLifecycle.addObserver(new GenericLifecycleObserver()
      {
        public void onStateChanged(LifecycleOwner paramAnonymousLifecycleOwner, Lifecycle.Event paramAnonymousEvent)
        {
          if (paramAnonymousEvent == Lifecycle.Event.ON_START)
          {
            mAllowingSavingState = true;
            return;
          }
          if (paramAnonymousEvent == Lifecycle.Event.ON_STOP) {
            mAllowingSavingState = false;
          }
        }
      });
      mRestored = true;
      return;
    }
    throw new IllegalStateException("SavedStateRegistry was already restored.");
  }
  
  void performSave(Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    if (mRestoredState != null) {
      localBundle.putAll(mRestoredState);
    }
    SafeIterableMap.IteratorWithAdditions localIteratorWithAdditions = mComponents.iteratorWithAdditions();
    while (localIteratorWithAdditions.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIteratorWithAdditions.next();
      localBundle.putBundle((String)localEntry.getKey(), ((SavedStateProvider)localEntry.getValue()).saveState());
    }
    paramBundle.putBundle("androidx.lifecycle.BundlableSavedStateRegistry.key", localBundle);
  }
  
  public void registerSavedStateProvider(String paramString, SavedStateProvider paramSavedStateProvider)
  {
    if ((SavedStateProvider)mComponents.putIfAbsent(paramString, paramSavedStateProvider) == null) {
      return;
    }
    throw new IllegalArgumentException("SavedStateProvider with the given key is already registered");
  }
  
  public void runOnNextRecreation(Class paramClass)
  {
    if (mAllowingSavingState)
    {
      if (mRecreatorProvider == null) {
        mRecreatorProvider = new Recreator.SavedStateProvider(this);
      }
      try
      {
        paramClass.getDeclaredConstructor(new Class[0]);
        mRecreatorProvider.setRemote(paramClass.getName());
        return;
      }
      catch (NoSuchMethodException localNoSuchMethodException)
      {
        StringBuilder localStringBuilder = new StringBuilder();
        localStringBuilder.append("Class");
        localStringBuilder.append(paramClass.getSimpleName());
        localStringBuilder.append(" must have default constructor in order to be automatically recreated");
        throw new IllegalArgumentException(localStringBuilder.toString(), localNoSuchMethodException);
      }
    }
    throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
  }
  
  public void unregisterSavedStateProvider(String paramString)
  {
    mComponents.remove(paramString);
  }
  
  public static abstract interface AutoRecreated
  {
    public abstract void onRecreated(SavedStateRegistryOwner paramSavedStateRegistryOwner);
  }
  
  public static abstract interface SavedStateProvider
  {
    public abstract Bundle saveState();
  }
}

package androidx.activity;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import androidx.annotation.LayoutRes;
import androidx.lifecycle.HasDefaultViewModelProviderFactory;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ReportFragment;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider.Factory;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.savedstate.SavedStateRegistry;
import androidx.savedstate.SavedStateRegistryController;
import androidx.savedstate.SavedStateRegistryOwner;

public class ComponentActivity
  extends androidx.core.package_4.ComponentActivity
  implements LifecycleOwner, ViewModelStoreOwner, HasDefaultViewModelProviderFactory, SavedStateRegistryOwner, OnBackPressedDispatcherOwner
{
  @LayoutRes
  private int mContentLayoutId;
  private ViewModelProvider.Factory mDefaultFactory;
  private final LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
  private final OnBackPressedDispatcher mOnBackPressedDispatcher = new OnBackPressedDispatcher(new Runnable()
  {
    public void run()
    {
      ComponentActivity.this.onBackPressed();
    }
  });
  private final SavedStateRegistryController mSavedStateRegistryController = SavedStateRegistryController.create(this);
  private ViewModelStore mViewModelStore;
  
  public ComponentActivity()
  {
    if (getLifecycle() != null)
    {
      if (Build.VERSION.SDK_INT >= 19) {
        getLifecycle().addObserver(new LifecycleEventObserver()
        {
          public void onStateChanged(LifecycleOwner paramAnonymousLifecycleOwner, Lifecycle.Event paramAnonymousEvent)
          {
            if (paramAnonymousEvent == Lifecycle.Event.ON_STOP)
            {
              paramAnonymousLifecycleOwner = getWindow();
              if (paramAnonymousLifecycleOwner != null) {
                paramAnonymousLifecycleOwner = paramAnonymousLifecycleOwner.peekDecorView();
              } else {
                paramAnonymousLifecycleOwner = null;
              }
              if (paramAnonymousLifecycleOwner != null) {
                paramAnonymousLifecycleOwner.cancelPendingInputEvents();
              }
            }
          }
        });
      }
      getLifecycle().addObserver(new LifecycleEventObserver()
      {
        public void onStateChanged(LifecycleOwner paramAnonymousLifecycleOwner, Lifecycle.Event paramAnonymousEvent)
        {
          if ((paramAnonymousEvent == Lifecycle.Event.ON_DESTROY) && (!isChangingConfigurations())) {
            getViewModelStore().clear();
          }
        }
      });
      if ((19 <= Build.VERSION.SDK_INT) && (Build.VERSION.SDK_INT <= 23)) {
        getLifecycle().addObserver(new ImmLeaksCleaner(this));
      }
    }
    else
    {
      throw new IllegalStateException("getLifecycle() returned null in ComponentActivity's constructor. Please make sure you are lazily constructing your Lifecycle in the first call to getLifecycle() rather than relying on field initialization.");
    }
  }
  
  public ComponentActivity(int paramInt)
  {
    this();
    mContentLayoutId = paramInt;
  }
  
  public ViewModelProvider.Factory getDefaultViewModelProviderFactory()
  {
    if (getApplication() != null)
    {
      if (mDefaultFactory == null)
      {
        Application localApplication = getApplication();
        Bundle localBundle;
        if (getIntent() != null) {
          localBundle = getIntent().getExtras();
        } else {
          localBundle = null;
        }
        mDefaultFactory = new SavedStateViewModelFactory(localApplication, this, localBundle);
      }
      return mDefaultFactory;
    }
    throw new IllegalStateException("Your activity is not yet attached to the Application instance. You can't request ViewModel before onCreate call.");
  }
  
  public Object getLastCustomNonConfigurationInstance()
  {
    NonConfigurationInstances localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
    if (localNonConfigurationInstances != null) {
      return custom;
    }
    return null;
  }
  
  public Lifecycle getLifecycle()
  {
    return mLifecycleRegistry;
  }
  
  public final OnBackPressedDispatcher getOnBackPressedDispatcher()
  {
    return mOnBackPressedDispatcher;
  }
  
  public final SavedStateRegistry getSavedStateRegistry()
  {
    return mSavedStateRegistryController.getSavedStateRegistry();
  }
  
  public ViewModelStore getViewModelStore()
  {
    if (getApplication() != null)
    {
      if (mViewModelStore == null)
      {
        NonConfigurationInstances localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
        if (localNonConfigurationInstances != null) {
          mViewModelStore = viewModelStore;
        }
        if (mViewModelStore == null) {
          mViewModelStore = new ViewModelStore();
        }
      }
      return mViewModelStore;
    }
    throw new IllegalStateException("Your activity is not yet attached to the Application instance. You can't request ViewModel before onCreate call.");
  }
  
  public void onBackPressed()
  {
    mOnBackPressedDispatcher.onBackPressed();
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    mSavedStateRegistryController.performRestore(paramBundle);
    ReportFragment.injectIfNeededIn(this);
    if (mContentLayoutId != 0) {
      setContentView(mContentLayoutId);
    }
  }
  
  public Object onRetainCustomNonConfigurationInstance()
  {
    return null;
  }
  
  public final Object onRetainNonConfigurationInstance()
  {
    Object localObject3 = onRetainCustomNonConfigurationInstance();
    Object localObject2 = mViewModelStore;
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      NonConfigurationInstances localNonConfigurationInstances = (NonConfigurationInstances)getLastNonConfigurationInstance();
      localObject1 = localObject2;
      if (localNonConfigurationInstances != null) {
        localObject1 = viewModelStore;
      }
    }
    if ((localObject1 == null) && (localObject3 == null)) {
      return null;
    }
    localObject2 = new NonConfigurationInstances();
    custom = localObject3;
    viewModelStore = ((ViewModelStore)localObject1);
    return localObject2;
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    Lifecycle localLifecycle = getLifecycle();
    if ((localLifecycle instanceof LifecycleRegistry)) {
      ((LifecycleRegistry)localLifecycle).setCurrentState(Lifecycle.State.CREATED);
    }
    super.onSaveInstanceState(paramBundle);
    mSavedStateRegistryController.performSave(paramBundle);
  }
  
  static final class NonConfigurationInstances
  {
    Object custom;
    ViewModelStore viewModelStore;
    
    NonConfigurationInstances() {}
  }
}

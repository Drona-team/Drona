package androidx.core.package_4;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import androidx.annotation.RestrictTo;
import androidx.collection.SimpleArrayMap;
import androidx.core.view.KeyEventDispatcher;
import androidx.core.view.KeyEventDispatcher.Component;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Lifecycle.State;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ReportFragment;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class ComponentActivity
  extends Activity
  implements LifecycleOwner, KeyEventDispatcher.Component
{
  private SimpleArrayMap<Class<? extends androidx.core.app.ComponentActivity.ExtraData>, androidx.core.app.ComponentActivity.ExtraData> mExtraDataMap = new SimpleArrayMap();
  private LifecycleRegistry mLifecycleRegistry = new LifecycleRegistry(this);
  
  public ComponentActivity() {}
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    View localView = getWindow().getDecorView();
    if ((localView != null) && (KeyEventDispatcher.dispatchBeforeHierarchy(localView, paramKeyEvent))) {
      return true;
    }
    return KeyEventDispatcher.dispatchKeyEvent(this, localView, this, paramKeyEvent);
  }
  
  public boolean dispatchKeyShortcutEvent(KeyEvent paramKeyEvent)
  {
    View localView = getWindow().getDecorView();
    if ((localView != null) && (KeyEventDispatcher.dispatchBeforeHierarchy(localView, paramKeyEvent))) {
      return true;
    }
    return super.dispatchKeyShortcutEvent(paramKeyEvent);
  }
  
  public ExtraData getExtraData(Class paramClass)
  {
    return (ExtraData)mExtraDataMap.get(paramClass);
  }
  
  public Lifecycle getLifecycle()
  {
    return mLifecycleRegistry;
  }
  
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    ReportFragment.injectIfNeededIn(this);
  }
  
  protected void onSaveInstanceState(Bundle paramBundle)
  {
    mLifecycleRegistry.markState(Lifecycle.State.CREATED);
    super.onSaveInstanceState(paramBundle);
  }
  
  public void putExtraData(ExtraData paramExtraData)
  {
    mExtraDataMap.put(paramExtraData.getClass(), paramExtraData);
  }
  
  public boolean superDispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return super.dispatchKeyEvent(paramKeyEvent);
  }
  
  @RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
  public class ExtraData
  {
    public ExtraData() {}
  }
}

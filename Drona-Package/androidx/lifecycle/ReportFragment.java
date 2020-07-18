package androidx.lifecycle;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build.VERSION;
import android.os.Bundle;
import androidx.annotation.RestrictTo;

@RestrictTo({androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX})
public class ReportFragment
  extends Fragment
{
  private static final String REPORT_FRAGMENT_TAG = "androidx.lifecycle.LifecycleDispatcher.report_fragment_tag";
  private ActivityInitializationListener mProcessListener;
  
  public ReportFragment() {}
  
  static void dispatch(Activity paramActivity, Lifecycle.Event paramEvent)
  {
    if ((paramActivity instanceof LifecycleRegistryOwner))
    {
      ((LifecycleRegistryOwner)paramActivity).getLifecycle().handleLifecycleEvent(paramEvent);
      return;
    }
    if ((paramActivity instanceof LifecycleOwner))
    {
      paramActivity = ((LifecycleOwner)paramActivity).getLifecycle();
      if ((paramActivity instanceof LifecycleRegistry)) {
        ((LifecycleRegistry)paramActivity).handleLifecycleEvent(paramEvent);
      }
    }
  }
  
  private void dispatch(Lifecycle.Event paramEvent)
  {
    if (Build.VERSION.SDK_INT < 29) {
      dispatch(getActivity(), paramEvent);
    }
  }
  
  private void dispatchCreate(ActivityInitializationListener paramActivityInitializationListener)
  {
    if (paramActivityInitializationListener != null) {
      paramActivityInitializationListener.onCreate();
    }
  }
  
  private void dispatchResume(ActivityInitializationListener paramActivityInitializationListener)
  {
    if (paramActivityInitializationListener != null) {
      paramActivityInitializationListener.onResume();
    }
  }
  
  private void dispatchStart(ActivityInitializationListener paramActivityInitializationListener)
  {
    if (paramActivityInitializationListener != null) {
      paramActivityInitializationListener.onStart();
    }
  }
  
  public static void injectIfNeededIn(Activity paramActivity)
  {
    if (Build.VERSION.SDK_INT >= 29) {
      paramActivity.registerActivityLifecycleCallbacks(new LifecycleCallbacks());
    }
    paramActivity = paramActivity.getFragmentManager();
    if (paramActivity.findFragmentByTag("androidx.lifecycle.LifecycleDispatcher.report_fragment_tag") == null)
    {
      paramActivity.beginTransaction().add(new ReportFragment(), "androidx.lifecycle.LifecycleDispatcher.report_fragment_tag").commit();
      paramActivity.executePendingTransactions();
    }
  }
  
  static ReportFragment showDialog(Activity paramActivity)
  {
    return (ReportFragment)paramActivity.getFragmentManager().findFragmentByTag("androidx.lifecycle.LifecycleDispatcher.report_fragment_tag");
  }
  
  public void onActivityCreated(Bundle paramBundle)
  {
    super.onActivityCreated(paramBundle);
    dispatchCreate(mProcessListener);
    dispatch(Lifecycle.Event.ON_CREATE);
  }
  
  public void onDestroy()
  {
    super.onDestroy();
    dispatch(Lifecycle.Event.ON_DESTROY);
    mProcessListener = null;
  }
  
  public void onPause()
  {
    super.onPause();
    dispatch(Lifecycle.Event.ON_PAUSE);
  }
  
  public void onResume()
  {
    super.onResume();
    dispatchResume(mProcessListener);
    dispatch(Lifecycle.Event.ON_RESUME);
  }
  
  public void onStart()
  {
    super.onStart();
    dispatchStart(mProcessListener);
    dispatch(Lifecycle.Event.ON_START);
  }
  
  public void onStop()
  {
    super.onStop();
    dispatch(Lifecycle.Event.ON_STOP);
  }
  
  void setProcessListener(ActivityInitializationListener paramActivityInitializationListener)
  {
    mProcessListener = paramActivityInitializationListener;
  }
  
  static abstract interface ActivityInitializationListener
  {
    public abstract void onCreate();
    
    public abstract void onResume();
    
    public abstract void onStart();
  }
  
  static class LifecycleCallbacks
    implements Application.ActivityLifecycleCallbacks
  {
    LifecycleCallbacks() {}
    
    public void onActivityCreated(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityDestroyed(Activity paramActivity) {}
    
    public void onActivityPaused(Activity paramActivity) {}
    
    public void onActivityPostCreated(Activity paramActivity, Bundle paramBundle)
    {
      ReportFragment.dispatch(paramActivity, Lifecycle.Event.ON_CREATE);
    }
    
    public void onActivityPostResumed(Activity paramActivity)
    {
      ReportFragment.dispatch(paramActivity, Lifecycle.Event.ON_RESUME);
    }
    
    public void onActivityPostStarted(Activity paramActivity)
    {
      ReportFragment.dispatch(paramActivity, Lifecycle.Event.ON_START);
    }
    
    public void onActivityPreDestroyed(Activity paramActivity)
    {
      ReportFragment.dispatch(paramActivity, Lifecycle.Event.ON_DESTROY);
    }
    
    public void onActivityPrePaused(Activity paramActivity)
    {
      ReportFragment.dispatch(paramActivity, Lifecycle.Event.ON_PAUSE);
    }
    
    public void onActivityPreStopped(Activity paramActivity)
    {
      ReportFragment.dispatch(paramActivity, Lifecycle.Event.ON_STOP);
    }
    
    public void onActivityResumed(Activity paramActivity) {}
    
    public void onActivitySaveInstanceState(Activity paramActivity, Bundle paramBundle) {}
    
    public void onActivityStarted(Activity paramActivity) {}
    
    public void onActivityStopped(Activity paramActivity) {}
  }
}

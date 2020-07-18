package com.google.android.gms.common.package_6.internal;

import android.app.Activity;
import android.content.ContextWrapper;
import androidx.fragment.package_5.FragmentActivity;
import com.google.android.gms.common.annotation.KeepForSdk;
import com.google.android.gms.common.internal.Preconditions;

@KeepForSdk
public class LifecycleActivity
{
  private final Object zzbd;
  
  public LifecycleActivity(Activity paramActivity)
  {
    Preconditions.checkNotNull(paramActivity, "Activity must not be null");
    zzbd = paramActivity;
  }
  
  public LifecycleActivity(ContextWrapper paramContextWrapper)
  {
    throw new UnsupportedOperationException();
  }
  
  public Activity asActivity()
  {
    return (Activity)zzbd;
  }
  
  public FragmentActivity asFragmentActivity()
  {
    return (FragmentActivity)zzbd;
  }
  
  public Object asObject()
  {
    return zzbd;
  }
  
  public boolean isChimera()
  {
    return false;
  }
  
  public boolean isSupport()
  {
    return zzbd instanceof FragmentActivity;
  }
  
  public final boolean join()
  {
    return zzbd instanceof Activity;
  }
}

package com.google.android.gms.common.package_6.internal;

import android.app.Activity;
import com.google.android.gms.common.annotation.KeepForSdk;

@KeepForSdk
public abstract class ActivityLifecycleObserver
{
  public ActivityLifecycleObserver() {}
  
  public static final ActivityLifecycleObserver getObject(Activity paramActivity)
  {
    return new ASN1OctetString(paramActivity);
  }
  
  public abstract ActivityLifecycleObserver onStopCallOnce(Runnable paramRunnable);
}

package com.google.android.gms.common.package_6.internal;

import android.app.Activity;
import java.lang.ref.WeakReference;

public final class ASN1OctetString
  extends ActivityLifecycleObserver
{
  private final WeakReference<com.google.android.gms.common.api.internal.zaa.zaa> zacl;
  
  public ASN1OctetString(Activity paramActivity)
  {
    this(zaa.zaa.descend(paramActivity));
  }
  
  private ASN1OctetString(zaa.zaa paramZaa)
  {
    zacl = new WeakReference(paramZaa);
  }
  
  public final ActivityLifecycleObserver onStopCallOnce(Runnable paramRunnable)
  {
    zaa.zaa localZaa = (zaa.zaa)zacl.get();
    if (localZaa != null)
    {
      zaa.zaa.runInBackground(localZaa, paramRunnable);
      return this;
    }
    throw new IllegalStateException("The target activity has already been GC'd");
  }
}
